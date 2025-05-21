using DAC_API.Models.DTO.User;

namespace DAC_API.Models.DTO.Authentication {
    public class AuthResponseDTO {
        public bool IsSuccess { get; set; }
        public string Message { get; set; }
        public string Token { get; set; }
        public string RefreshToken { get; set; }
        public DateTime Expiration { get; set; }
        public UserResponseDTO User { get; set; }
    }
}
