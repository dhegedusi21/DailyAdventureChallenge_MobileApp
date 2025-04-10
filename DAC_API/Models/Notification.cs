﻿using System;
using System.Collections.Generic;

namespace DAC_API.Models;

public partial class Notification
{
    public int IdNotification { get; set; }

    public int UserId { get; set; }

    public string? Name { get; set; }

    public string? Message { get; set; }

    public string? Type { get; set; }

    public bool? IsRead { get; set; }

    public DateTime? CreatedAt { get; set; }

    public virtual User User { get; set; } = null!;
}
