using System;
using System.Collections.Generic;

namespace DAC_API.Models;

public partial class UserChallenge {
    public int IdUserChallenge { get; set; }

    public int UserId { get; set; }

    public int ChallengeId { get; set; }

    public DateTime AssignedDate { get; set; }

    public string CompletionStatus { get; set; } = "Active"; // Active, Completed, Expired

    public virtual Challenge Challenge { get; set; } = null!;

    public virtual User User { get; set; } = null!;
}
