using DAC_API.Models;
using DAC_API.Models.DTO;
using DAC_API.Models.DTO.Authentication;
using DAC_API.Models.DTO.User;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using System;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Security.Cryptography;
using System.Text;

namespace DAC_API.Controllers {
    [Route("api/[controller]")]
    [ApiController]
    public class AuthController : ControllerBase {
        private readonly DailyAdventureAppContext _context;
        private readonly IConfiguration _configuration;

        public AuthController(DailyAdventureAppContext context, IConfiguration configuration) {
            _context = context;
            _configuration = configuration;
        }

        [HttpPost("login")]
        public async Task<ActionResult<AuthResponseDTO>> Login(LoginDTO model) {
            if (!ModelState.IsValid) {
                return BadRequest(ModelState);
            }

            try {
                var user = await _context.Users
                    .FirstOrDefaultAsync(u => u.Email == model.Email);

                if (user == null) {
                    return Unauthorized(new AuthResponseDTO {
                        IsSuccess = false,
                        Message = "Invalid email or password"
                    });
                }

                if (user.Password != model.Password) {
                    return Unauthorized(new AuthResponseDTO {
                        IsSuccess = false,
                        Message = "Invalid email or password"
                    });
                }

                var token = GenerateJwtToken(user);
                var refreshToken = GenerateRefreshToken();
                var tokenExpiryTime = DateTime.Now.AddMinutes(
                    Convert.ToDouble(_configuration["JWT:TokenValidityInMinutes"]));

                user.RefreshToken = refreshToken;
                user.RefreshTokenExpiryTime = DateTime.Now.AddDays(
                    Convert.ToDouble(_configuration["JWT:RefreshTokenValidityInDays"]));

                _context.Entry(user).State = EntityState.Modified;
                await _context.SaveChangesAsync();

                var userDto = new UserResponseDTO {
                    IdUser = user.IdUser,
                    Username = user.Username,
                    Email = user.Email,
                    ProfilePicture = user.ProfilePicture,
                    CreatedAt = user.CreatedAt
                };

                return Ok(new AuthResponseDTO {
                    IsSuccess = true,
                    Token = token,
                    RefreshToken = refreshToken,
                    Expiration = tokenExpiryTime,
                    User = userDto,
                    Message = "Login successful"
                });
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    new AuthResponseDTO {
                        IsSuccess = false,
                        Message = "Error during login: " + ex.Message
                    });
            }
        }

        [HttpPost("register")]
        public async Task<ActionResult<AuthResponseDTO>> Register(CreateUserDTO model) {
            if (!ModelState.IsValid) {
                return BadRequest(ModelState);
            }

            try {
                var emailExists = await _context.Users.AnyAsync(u => u.Email == model.Email);
                if (emailExists) {
                    return BadRequest(new AuthResponseDTO {
                        IsSuccess = false,
                        Message = "Email already exists"
                    });
                }

                var usernameExists = await _context.Users.AnyAsync(u => u.Username == model.Username);
                if (usernameExists) {
                    return BadRequest(new AuthResponseDTO {
                        IsSuccess = false,
                        Message = "Username already exists"
                    });
                }

                var user = new User {
                    Username = model.Username,
                    Email = model.Email,
                    Password = model.Password,
                    ProfilePicture = model.ProfilePicture,
                    CreatedAt = DateTime.Now
                };

                _context.Users.Add(user);
                await _context.SaveChangesAsync();

                var token = GenerateJwtToken(user);
                var refreshToken = GenerateRefreshToken();
                var tokenExpiryTime = DateTime.Now.AddMinutes(
                    Convert.ToDouble(_configuration["JWT:TokenValidityInMinutes"]));

                user.RefreshToken = refreshToken;
                user.RefreshTokenExpiryTime = DateTime.Now.AddDays(
                    Convert.ToDouble(_configuration["JWT:RefreshTokenValidityInDays"]));

                _context.Entry(user).State = EntityState.Modified;
                await _context.SaveChangesAsync();

                var userDto = new UserResponseDTO {
                    IdUser = user.IdUser,
                    Username = user.Username,
                    Email = user.Email,
                    ProfilePicture = user.ProfilePicture,
                    CreatedAt = user.CreatedAt
                };

                return Ok(new AuthResponseDTO {
                    IsSuccess = true,
                    Token = token,
                    RefreshToken = refreshToken,
                    Expiration = tokenExpiryTime,
                    User = userDto,
                    Message = "Registration successful"
                });
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    new AuthResponseDTO {
                        IsSuccess = false,
                        Message = "Error during registration: " + ex.Message
                    });
            }
        }

        [HttpPost("refresh-token")]
        public async Task<ActionResult<AuthResponseDTO>> RefreshToken(TokenModel tokenModel) {
            if (tokenModel is null) {
                return BadRequest("Invalid client request");
            }

            string accessToken = tokenModel.AccessToken;
            string refreshToken = tokenModel.RefreshToken;

            var principal = GetPrincipalFromExpiredToken(accessToken);
            if (principal == null) {
                return BadRequest("Invalid access token or refresh token");
            }

            string userId = principal.FindFirst(ClaimTypes.NameIdentifier)?.Value;

            var user = await _context.Users.FindAsync(int.Parse(userId));

            // Check if user exists and has a valid refresh token
            if (user == null || user.RefreshToken != refreshToken || !user.RefreshTokenExpiryTime.HasValue || user.RefreshTokenExpiryTime.Value <= DateTime.Now) {
                return BadRequest("Invalid access token or refresh token");
            }

            var newAccessToken = GenerateJwtToken(user);
            var newRefreshToken = GenerateRefreshToken();
            var tokenExpiryTime = DateTime.Now.AddMinutes(
                Convert.ToDouble(_configuration["JWT:TokenValidityInMinutes"]));

            user.RefreshToken = newRefreshToken;
            user.RefreshTokenExpiryTime = DateTime.Now.AddDays(
                Convert.ToDouble(_configuration["JWT:RefreshTokenValidityInDays"]));

            _context.Entry(user).State = EntityState.Modified;
            await _context.SaveChangesAsync();

            var userDto = new UserResponseDTO {
                IdUser = user.IdUser,
                Username = user.Username,
                Email = user.Email,
                ProfilePicture = user.ProfilePicture,
                CreatedAt = user.CreatedAt
            };

            return Ok(new AuthResponseDTO {
                IsSuccess = true,
                Token = newAccessToken,
                RefreshToken = newRefreshToken,
                Expiration = tokenExpiryTime,
                User = userDto,
                Message = "Token refreshed successfully"
            });
        }

        private string GenerateJwtToken(User user) {
            var securityKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_configuration["JWT:Secret"]));
            var credentials = new SigningCredentials(securityKey, SecurityAlgorithms.HmacSha256);

            var claims = new[]
            {
                new Claim(ClaimTypes.NameIdentifier, user.IdUser.ToString()),
                new Claim(ClaimTypes.Name, user.Username),
                new Claim(ClaimTypes.Email, user.Email),
                new Claim(JwtRegisteredClaimNames.Jti, Guid.NewGuid().ToString())
            };

            var token = new JwtSecurityToken(
                issuer: _configuration["JWT:ValidIssuer"],
                audience: _configuration["JWT:ValidAudience"],
                claims: claims,
                expires: DateTime.Now.AddMinutes(Convert.ToDouble(_configuration["JWT:TokenValidityInMinutes"])),
                signingCredentials: credentials);

            return new JwtSecurityTokenHandler().WriteToken(token);
        }

        private string GenerateRefreshToken() {
            var randomNumber = new byte[64];
            using var rng = RandomNumberGenerator.Create();
            rng.GetBytes(randomNumber);
            return Convert.ToBase64String(randomNumber);
        }

        private ClaimsPrincipal GetPrincipalFromExpiredToken(string token) {
            var tokenValidationParameters = new TokenValidationParameters {
                ValidateAudience = false,
                ValidateIssuer = false,
                ValidateIssuerSigningKey = true,
                IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_configuration["JWT:Secret"])),
                ValidateLifetime = false
            };

            var tokenHandler = new JwtSecurityTokenHandler();
            var principal = tokenHandler.ValidateToken(token, tokenValidationParameters, out SecurityToken securityToken);

            if (securityToken is not JwtSecurityToken jwtSecurityToken ||
                !jwtSecurityToken.Header.Alg.Equals(SecurityAlgorithms.HmacSha256, StringComparison.InvariantCultureIgnoreCase)) {
                throw new SecurityTokenException("Invalid token");
            }

            return principal;
        }
    }

}
