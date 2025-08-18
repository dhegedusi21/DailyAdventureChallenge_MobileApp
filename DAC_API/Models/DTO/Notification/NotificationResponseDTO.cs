using System;

namespace DAC_API.Models.DTO.Notification {
    public class NotificationResponseDTO {
        public int IdNotification { get; set; }
        public int UserId { get; set; }
        public string? Name { get; set; }
        public string? Message { get; set; }
        public string? Type { get; set; }
        public bool IsRead { get; set; }
        public DateTime? CreatedAt { get; set; }
    }
}
