using System.ComponentModel.DataAnnotations;

namespace DAC_API.Models.DTO.Vote {
    public class CreateVoteDTO {
        [Required(ErrorMessage = "Submission ID is required")]
        public int SubmissionId { get; set; }

        [Required(ErrorMessage = "User ID is required")]
        public int UserId { get; set; }

        [Required(ErrorMessage = "Vote status is required")]
        [RegularExpression("^(Upvote|Downvote)$", ErrorMessage = "Vote status must be 'Upvote' or 'Downvote'")]
        public string VoteStatus { get; set; }
    }
}
