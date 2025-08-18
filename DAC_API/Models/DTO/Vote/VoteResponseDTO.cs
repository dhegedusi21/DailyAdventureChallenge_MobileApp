using System;

namespace DAC_API.Models.DTO.Vote {
    public class VoteResponseDTO {
        public int IdVote { get; set; }
        public int SubmissionId { get; set; }
        public int UserId { get; set; }
        public string VoteStatus { get; set; }
        public DateTime? CreatedAt { get; set; }

        public string Username { get; set; }
        public string SubmissionPhotoUrl { get; set; }
    }
}
