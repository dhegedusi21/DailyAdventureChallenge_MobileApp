using System.ComponentModel.DataAnnotations;

namespace DAC_API.Models.DTO.Vote {
    public class UpdateVoteDTO {
        public int IdVote { get; set; }

        [Required(ErrorMessage = "Vote status is required")]
        [RegularExpression("^(Positive|Negative)$", ErrorMessage = "Vote status must be 'Positive' or 'Negative'")]
        public string VoteStatus { get; set; }
    }
}
