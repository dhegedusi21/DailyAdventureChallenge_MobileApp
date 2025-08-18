using System.ComponentModel.DataAnnotations;

namespace DAC_API.Models.DTO.User {
    public class CreateUserDTO {
        [Required(ErrorMessage = "Username is required")]
        [StringLength(50, MinimumLength = 3, ErrorMessage = "Username must be between 3 and 50 characters")]
        public string Username { get; set; }

        [Required(ErrorMessage = "Email is required")]
        [EmailAddress(ErrorMessage = "Invalid email format")]
        public string Email { get; set; }

        [Required(ErrorMessage = "Password is required")]
        [StringLength(255, MinimumLength = 4, ErrorMessage = "Password must be at least 4 characters")]
        public string Password { get; set; }

        public string? ProfilePicture { get; set; }
    }
}
