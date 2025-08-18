using System;
using System.Collections.Generic;

namespace DAC_API.Models;

public partial class Submission
{
    public int IdSubmission { get; set; }

    public int UserId { get; set; }

    public int ChallengeId { get; set; }

    public string PhotoUrl { get; set; } = null!;

    public string? Status { get; set; }

    public DateTime? CreatedAt { get; set; }

    public virtual Challenge Challenge { get; set; } = null!;

    public virtual User User { get; set; } = null!;

    public virtual ICollection<Vote> Votes { get; set; } = new List<Vote>();
}
