using System;

namespace DAC_API.Models.DTO.UserAchievement {
    public class UserAchievementResponseDTO {
        public int UserId { get; set; }
        public int AchievementId { get; set; }
        public DateTime? EarnedAt { get; set; }

        public string Username { get; set; }
        public string AchievementName { get; set; }
        public string AchievementRequirements { get; set; }
    }
}
