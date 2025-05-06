using System.ComponentModel.DataAnnotations;

namespace DAC_API.Models.DTO.Notification {
    public class CreateNotificationDTO {
        [Required(ErrorMessage = "User ID is required")]
        public int UserId { get; set; }

        public string? Name { get; set; }

        [Required(ErrorMessage = "Message is required")]
        public string Message { get; set; }

        public string? Type { get; set; }
    }
}
