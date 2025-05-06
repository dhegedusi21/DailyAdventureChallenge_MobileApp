using System;

namespace DAC_API.Models.DTO.Achievement
{
    public class AchievementResponseDTO
    {
        public int IdAchievement { get; set; }
        public string Name { get; set; }
        public string Requirements { get; set; }
    }
}