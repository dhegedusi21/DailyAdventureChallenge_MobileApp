using System.ComponentModel.DataAnnotations;

namespace DAC_API.Models.DTO.Vote {
    public class UpdateVoteDTO {
        public int IdVote { get; set; }

        [Required(ErrorMessage = "Vote status is required")]
        [RegularExpression("^(Upvote|Downvote)$", ErrorMessage = "Vote status must be 'Upvote' or 'Downvote'")]
        public string VoteStatus { get; set; }
    }
}
