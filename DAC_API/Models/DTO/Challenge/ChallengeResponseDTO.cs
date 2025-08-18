namespace DAC_API.Models.DTO.Challenge {
    public class ChallengeResponseDTO {
        public int IdChallenge { get; set; }
        public string? Description { get; set; }
        public string? Difficulty { get; set; }
        public int? Points { get; set; }
    }
}
