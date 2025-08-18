using CloudinaryDotNet;
using CloudinaryDotNet.Actions;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;

namespace DAC_API.Controllers {
    [ApiController]
    [Route("api/[controller]")]
    public class FileUploadController : ControllerBase {
        private readonly Cloudinary _cloudinary;

        public FileUploadController(IConfiguration configuration) {
            var account = new Account(
                configuration["Cloudinary:CloudName"],
                configuration["Cloudinary:ApiKey"],
                configuration["Cloudinary:ApiSecret"]
            );
            _cloudinary = new Cloudinary(account);
        }

        [Authorize]
        [HttpPost("upload-profile")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<object>> UploadProfilePicture(IFormFile file) {
            if (file == null || file.Length == 0) {
                return BadRequest("No file uploaded");
            }

            try {
                var userId = GetUserIdFromJWT();

                var uploadParams = new ImageUploadParams() {
                    File = new FileDescription(file.FileName, file.OpenReadStream()),
                    Folder = "profiles",
                    PublicId = $"user_{userId}_{Guid.NewGuid()}",
                    Transformation = new Transformation()
                        .Width(300).Height(300)
                        .Crop("fill")
                        .Quality("auto")
                        .FetchFormat("auto")
                };

                var uploadResult = await _cloudinary.UploadAsync(uploadParams);

                if (uploadResult.Error != null) {
                    return BadRequest($"Upload failed: {uploadResult.Error.Message}");
                }

                return Ok(new {
                    success = true,
                    url = uploadResult.SecureUrl.ToString(),
                    publicId = uploadResult.PublicId
                });

            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error uploading profile picture: " + ex.Message);
            }
        }

        [Authorize]
        [HttpPost("upload-submission")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<object>> UploadSubmissionPhoto(IFormFile file) {
            if (file == null || file.Length == 0) {
                return BadRequest("No file uploaded");
            }

            try {
                var userId = GetUserIdFromJWT();

                var uploadParams = new ImageUploadParams() {
                    File = new FileDescription(file.FileName, file.OpenReadStream()),
                    Folder = "submissions",
                    PublicId = $"submission_{userId}_{Guid.NewGuid()}",
                    Transformation = new Transformation()
                        .Width(800).Height(600)
                        .Crop("limit")
                        .Quality("auto")
                        .FetchFormat("auto")
                };

                var uploadResult = await _cloudinary.UploadAsync(uploadParams);

                if (uploadResult.Error != null) {
                    return BadRequest($"Upload failed: {uploadResult.Error.Message}");
                }

                return Ok(new {
                    success = true,
                    url = uploadResult.SecureUrl.ToString(),
                    publicId = uploadResult.PublicId
                });

            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error uploading submission photo: " + ex.Message);
            }
        }

        private int GetUserIdFromJWT() {
            var userIdClaim = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            if (string.IsNullOrEmpty(userIdClaim)) {
                throw new UnauthorizedAccessException("Invalid token: missing user ID");
            }
            return int.Parse(userIdClaim);
        }
    }
}
