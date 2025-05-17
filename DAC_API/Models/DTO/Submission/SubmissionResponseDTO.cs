using System;

namespace DAC_API.Models.DTO.Submission {
    public class SubmissionResponseDTO {
        public int IdSubmission { get; set; }
        public int UserId { get; set; }
        public int ChallengeId { get; set; }
        public string PhotoUrl { get; set; }
        public string Status { get; set; }
        public DateTime? CreatedAt { get; set; }

        public string Username { get; set; }
        public string ChallengeDescription { get; set; }
        public int VoteCount { get; set; }
    }
}
