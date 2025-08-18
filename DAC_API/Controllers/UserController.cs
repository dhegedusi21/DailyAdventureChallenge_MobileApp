using DAC_API.Models;
using DAC_API.Models.DTO.User;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Linq;

namespace DAC_API.Controllers {
    [ApiController]
    [Route("api/[controller]")]
    public class UserController : ControllerBase {
        private readonly DailyAdventureAppContext _context;

        public UserController(DailyAdventureAppContext context) {
            _context = context;
        }

        // Gets all users
        [HttpGet]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<IEnumerable<UserResponseDTO>>> GetAllUsers() {
            try {
                var users = await _context.Users.ToListAsync();
                var userDtos = users.Select(u => new UserResponseDTO {
                    IdUser = u.IdUser,
                    Username = u.Username,
                    Email = u.Email,
                    ProfilePicture = u.ProfilePicture,
                    CreatedAt = u.CreatedAt
                }).ToList();

                return Ok(userDtos);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error retrieving data from the database: " + ex.Message);
            }
        }

        // Gets a user by ID
        [HttpGet("{id}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<UserResponseDTO>> GetUserById(int id) {
            try {
                var user = await _context.Users.FindAsync(id);

                if (user == null) {
                    return NotFound($"User with ID {id} not found");
                }

                var userDto = new UserResponseDTO {
                    IdUser = user.IdUser,
                    Username = user.Username,
                    Email = user.Email,
                    ProfilePicture = user.ProfilePicture,
                    CreatedAt = user.CreatedAt
                };

                return Ok(userDto);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error retrieving data from the database: " + ex.Message);
            }
        }

        // Creates a user
        [HttpPost]
        [ProducesResponseType(StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<UserResponseDTO>> CreateUser(CreateUserDTO createUserDto) {
            if (!ModelState.IsValid) {
                return BadRequest(ModelState);
            }

            try {
                var user = new User {
                    Username = createUserDto.Username,
                    Email = createUserDto.Email,
                    Password = createUserDto.Password,
                    ProfilePicture = createUserDto.ProfilePicture,
                    CreatedAt = DateTime.Now
                };

                _context.Users.Add(user);
                await _context.SaveChangesAsync();

                var userResponseDto = new UserResponseDTO {
                    IdUser = user.IdUser,
                    Username = user.Username,
                    Email = user.Email,
                    ProfilePicture = user.ProfilePicture,
                    CreatedAt = user.CreatedAt
                };

                return CreatedAtAction(nameof(GetUserById), new { id = user.IdUser }, userResponseDto);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error creating user: " + ex.Message);
            }
        }


        // Updates a user
        [Authorize]
        [HttpPut("{id}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<IActionResult> UpdateUser(int id, UpdateUserDTO updateUserDto) {
            if (id != updateUserDto.IdUser) {
                return BadRequest("User ID mismatch");
            }

            if (!ModelState.IsValid) {
                return BadRequest(ModelState);
            }

            try {
                var existingUser = await _context.Users.FindAsync(id);

                if (existingUser == null) {
                    return NotFound($"User with ID {id} not found");
                }

                existingUser.Username = updateUserDto.Username;
                existingUser.Email = updateUserDto.Email;

                if (!string.IsNullOrEmpty(updateUserDto.Password)) {
                    existingUser.Password = updateUserDto.Password;
                }

                existingUser.ProfilePicture = updateUserDto.ProfilePicture;

                _context.Entry(existingUser).State = EntityState.Modified;

                await _context.SaveChangesAsync();

                return Ok(new { message = "User updated successfully" });
            } catch (DbUpdateConcurrencyException) {
                if (!UserExists(id)) {
                    return NotFound($"User with ID {id} not found");
                } else {
                    throw;
                }
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error updating user: " + ex.Message);
            }
        }

        // Deletes a user
        [Authorize]
        [HttpDelete("{id}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<IActionResult> DeleteUser(int id) {
            try {
                var user = await _context.Users.FindAsync(id);

                if (user == null) {
                    return NotFound($"User with ID {id} not found");
                }

                _context.Users.Remove(user);
                await _context.SaveChangesAsync();

                return NoContent();
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error deleting user: " + ex.Message);
            }
        }


        /// Helper methods
        private bool UserExists(int id) {
            return _context.Users.Any(e => e.IdUser == id);
        }
    }
}
