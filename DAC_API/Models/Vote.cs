using System;
using System.Collections.Generic;

namespace DAC_API.Models;

public partial class Vote
{
    public int IdVote { get; set; }

    public int SubmissionId { get; set; }

    public int UserId { get; set; }

    public string? VoteStatus { get; set; }

    public DateTime? CreatedAt { get; set; }

    public virtual Submission Submission { get; set; } = null!;

    public virtual User User { get; set; } = null!;
}
