using System;
using System.Collections.Generic;

namespace DAC_API.Models;

public partial class Achievement
{
    public int IdAchievement { get; set; }

    public string Name { get; set; } = null!;

    public string Requirements { get; set; } = null!;

    public virtual ICollection<UserAchievement> UserAchievements { get; set; } = new List<UserAchievement>();
}
