using System;
using System.Collections.Generic;

namespace DAC_API.Models;

public partial class User
{
    public int IdUser { get; set; }
    public string Username { get; set; } = null!;
    public string Email { get; set; } = null!;
    public string Password { get; set; } = null!;
    public string? ProfilePicture { get; set; }
    public int? Points { get; set; }
    public DateTime? CreatedAt { get; set; }

    public virtual ICollection<Notification> Notifications { get; set; } = new List<Notification>();

    public virtual ICollection<Submission> Submissions { get; set; } = new List<Submission>();

    public virtual ICollection<UserAchievement> UserAchievements { get; set; } = new List<UserAchievement>();

    public virtual ICollection<Vote> Votes { get; set; } = new List<Vote>();

    public string? RefreshToken { get; set; }
    public DateTime? RefreshTokenExpiryTime { get; set; }


}
