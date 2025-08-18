using System;

namespace DAC_API.Models.DTO.User {
    public class UserResponseDTO {
        public int IdUser { get; set; }
        public string Username { get; set; }
        public string Email { get; set; }
        public string? ProfilePicture { get; set; }
        public DateTime? CreatedAt { get; set; }
        public int? Points { get; set; }
    }
}
