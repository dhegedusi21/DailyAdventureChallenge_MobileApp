using System;
using System.Collections.Generic;

namespace DAC_API.Models;

public partial class Challenge
{
    public int IdChallenge { get; set; }

    public string? Description { get; set; }

    public string? Difficulty { get; set; }

    public int? Points { get; set; }

    public virtual ICollection<Submission> Submissions { get; set; } = new List<Submission>();
}
