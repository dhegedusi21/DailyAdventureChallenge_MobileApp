using System.ComponentModel.DataAnnotations;

namespace DAC_API.Models.DTO.Submission {
    public class CreateSubmissionDTO {
        [Required(ErrorMessage = "User ID is required")]
        public int UserId { get; set; }

        [Required(ErrorMessage = "Challenge ID is required")]
        public int ChallengeId { get; set; }

        [Required(ErrorMessage = "Photo URL is required")]
        [Url(ErrorMessage = "Invalid URL format")]
        public string PhotoUrl { get; set; }
    }
}
