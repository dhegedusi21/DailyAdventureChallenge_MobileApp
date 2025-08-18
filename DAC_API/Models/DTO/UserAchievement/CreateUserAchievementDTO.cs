using System.ComponentModel.DataAnnotations;

namespace DAC_API.Models.DTO.UserAchievement {
    public class CreateUserAchievementDTO {
        [Required(ErrorMessage = "User ID is required")]
        public int UserId { get; set; }

        [Required(ErrorMessage = "Achievement ID is required")]
        public int AchievementId { get; set; }
    }
}
