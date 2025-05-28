using System;
using System.Collections.Generic;
using Microsoft.EntityFrameworkCore;

namespace DAC_API.Models;

public partial class DailyAdventureAppContext : DbContext
{
    public DailyAdventureAppContext()
    {
    }

    public DailyAdventureAppContext(DbContextOptions<DailyAdventureAppContext> options)
        : base(options)
    {
    }

    public virtual DbSet<Achievement> Achievements { get; set; }

    public virtual DbSet<Challenge> Challenges { get; set; }

    public virtual DbSet<Notification> Notifications { get; set; }

    public virtual DbSet<Submission> Submissions { get; set; }

    public virtual DbSet<User> Users { get; set; }

    public virtual DbSet<UserAchievement> UserAchievements { get; set; }

    public virtual DbSet<Vote> Votes { get; set; }
    public virtual DbSet<UserChallenge> UserChallenges { get; set; }



    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
#warning To protect potentially sensitive information in your connection string, you should move it out of source code. You can avoid scaffolding the connection string by using the Name= syntax to read it from configuration - see https://go.microsoft.com/fwlink/?linkid=2131148. For more guidance on storing connection strings, see https://go.microsoft.com/fwlink/?LinkId=723263.
        => optionsBuilder.UseSqlServer("Server=localhost;Database=Daily_Adventure_App;Trusted_Connection=True;TrustServerCertificate=True;");

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Achievement>(entity =>
        {
            entity.HasKey(e => e.IdAchievement).HasName("PK__Achievem__6C7EE001CFBBCA13");

            entity.ToTable("Achievement");

            entity.Property(e => e.IdAchievement).HasColumnName("idAchievement");
            entity.Property(e => e.Name)
                .HasMaxLength(100)
                .HasColumnName("name");
            entity.Property(e => e.Requirements)
                .HasMaxLength(255)
                .HasColumnName("requirements");
        });

        modelBuilder.Entity<Challenge>(entity =>
        {
            entity.HasKey(e => e.IdChallenge).HasName("PK__Challeng__1DF81F90E6FA04D6");

            entity.ToTable("Challenge");

            entity.Property(e => e.IdChallenge).HasColumnName("idChallenge");
            entity.Property(e => e.Description).HasColumnName("description");
            entity.Property(e => e.Difficulty)
                .HasMaxLength(10)
                .HasColumnName("difficulty");
            entity.Property(e => e.Points).HasColumnName("points");
        });

        modelBuilder.Entity<Notification>(entity =>
        {
            entity.HasKey(e => e.IdNotification).HasName("PK__Notifica__22C023213095E4A9");

            entity.ToTable("Notification");

            entity.HasIndex(e => e.UserId, "idx_Notification_User");

            entity.Property(e => e.IdNotification).HasColumnName("idNotification");
            entity.Property(e => e.CreatedAt)
                .HasDefaultValueSql("(getdate())")
                .HasColumnType("datetime")
                .HasColumnName("created_at");
            entity.Property(e => e.IsRead)
                .HasDefaultValue(false)
                .HasColumnName("is_read");
            entity.Property(e => e.Message).HasColumnName("message");
            entity.Property(e => e.Name)
                .HasMaxLength(255)
                .HasColumnName("name");
            entity.Property(e => e.Type)
                .HasMaxLength(20)
                .HasColumnName("type");
            entity.Property(e => e.UserId).HasColumnName("user_id");

            entity.HasOne(d => d.User).WithMany(p => p.Notifications)
                .HasForeignKey(d => d.UserId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK__Notificat__user___5535A963");
        });

        modelBuilder.Entity<Submission>(entity =>
        {
            entity.HasKey(e => e.IdSubmission).HasName("PK__Submissi__11D39A5A0CB4E18E");

            entity.ToTable("Submission");

            entity.HasIndex(e => e.ChallengeId, "idx_Submission_Challenge");

            entity.HasIndex(e => e.UserId, "idx_Submission_User");

            entity.Property(e => e.IdSubmission).HasColumnName("idSubmission");
            entity.Property(e => e.ChallengeId).HasColumnName("challenge_id");
            entity.Property(e => e.CreatedAt)
                .HasDefaultValueSql("(getdate())")
                .HasColumnType("datetime")
                .HasColumnName("created_at");
            entity.Property(e => e.PhotoUrl)
                .HasMaxLength(255)
                .HasColumnName("photo_url");
            entity.Property(e => e.Status)
                .HasMaxLength(10)
                .HasDefaultValue("Pending")
                .HasColumnName("status");
            entity.Property(e => e.UserId).HasColumnName("user_id");

            entity.HasOne(d => d.Challenge).WithMany(p => p.Submissions)
                .HasForeignKey(d => d.ChallengeId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK__Submissio__chall__4316F928");

            entity.HasOne(d => d.User).WithMany(p => p.Submissions)
                .HasForeignKey(d => d.UserId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK__Submissio__user___4222D4EF");
        });

        modelBuilder.Entity<User>(entity =>
        {
            entity.HasKey(e => e.IdUser).HasName("PK__User__3717C982D76AEB7F");

            entity.ToTable("User");

            entity.HasIndex(e => e.Email, "UQ__User__AB6E61649EE87EC7").IsUnique();

            entity.HasIndex(e => e.Username, "UQ__User__F3DBC5722C4967EC").IsUnique();

            entity.Property(e => e.IdUser).HasColumnName("idUser");
            entity.Property(e => e.CreatedAt)
                .HasDefaultValueSql("(getdate())")
                .HasColumnType("datetime")
                .HasColumnName("created_at");
            entity.Property(e => e.Email)
                .HasMaxLength(100)
                .HasColumnName("email");
            entity.Property(e => e.Password)
                .HasMaxLength(255)
                .HasColumnName("password");
            entity.Property(e => e.ProfilePicture)
                .HasMaxLength(255)
                .HasColumnName("profile_picture");
            entity.Property(e => e.Username)
                .HasMaxLength(50)
                .HasColumnName("username");
            entity.Property(e => e.Points)
                .HasDefaultValue(0)
                .HasColumnName("points");
            entity.Property(e => e.RefreshToken)
                .HasMaxLength(255)
                .HasColumnName("refresh_token");
            entity.Property(e => e.RefreshTokenExpiryTime)
                .HasColumnType("datetime")
                .HasColumnName("refresh_token_expiry_time");
        });

        modelBuilder.Entity<UserAchievement>(entity =>
        {
            entity.HasKey(e => new { e.UserId, e.AchievementId }).HasName("PK__UserAchi__9A7AA5E7125E887B");

            entity.ToTable("UserAchievement");

            entity.HasIndex(e => e.AchievementId, "idx_UserAchievement_Achievement");

            entity.HasIndex(e => e.UserId, "idx_UserAchievement_User");

            entity.Property(e => e.UserId).HasColumnName("user_id");
            entity.Property(e => e.AchievementId).HasColumnName("achievement_id");
            entity.Property(e => e.EarnedAt)
                .HasDefaultValueSql("(getdate())")
                .HasColumnType("datetime")
                .HasColumnName("earned_at");

            entity.HasOne(d => d.Achievement).WithMany(p => p.UserAchievements)
                .HasForeignKey(d => d.AchievementId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK__UserAchie__achie__4F7CD00D");

            entity.HasOne(d => d.User).WithMany(p => p.UserAchievements)
                .HasForeignKey(d => d.UserId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK__UserAchie__user___4E88ABD4");
        });

        modelBuilder.Entity<Vote>(entity =>
        {
            entity.HasKey(e => e.IdVote).HasName("PK__Vote__0355858FBF5C9E84");

            entity.ToTable("Vote");

            entity.HasIndex(e => e.SubmissionId, "idx_Vote_Submission");

            entity.HasIndex(e => e.UserId, "idx_Vote_User");

            entity.Property(e => e.IdVote).HasColumnName("idVote");
            entity.Property(e => e.CreatedAt)
                .HasDefaultValueSql("(getdate())")
                .HasColumnType("datetime")
                .HasColumnName("created_at");
            entity.Property(e => e.SubmissionId).HasColumnName("submission_id");
            entity.Property(e => e.UserId).HasColumnName("user_id");
            entity.Property(e => e.VoteStatus)
                .HasMaxLength(10)
                .HasColumnName("vote_status");

            entity.HasOne(d => d.Submission).WithMany(p => p.Votes)
                .HasForeignKey(d => d.SubmissionId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK__Vote__submission__47DBAE45");

            entity.HasOne(d => d.User).WithMany(p => p.Votes)
                .HasForeignKey(d => d.UserId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK__Vote__user_id__48CFD27E");
        });
        modelBuilder.Entity<UserChallenge>(entity =>
        {
            entity.HasKey(e => e.IdUserChallenge).HasName("PK__UserChal__3214EC07");

            entity.ToTable("UserChallenge");

            entity.HasIndex(e => e.UserId, "idx_UserChallenge_User");
            entity.HasIndex(e => e.ChallengeId, "idx_UserChallenge_Challenge");

            entity.Property(e => e.IdUserChallenge).HasColumnName("idUserChallenge");
            entity.Property(e => e.UserId).HasColumnName("user_id");
            entity.Property(e => e.ChallengeId).HasColumnName("challenge_id");
            entity.Property(e => e.AssignedDate)
                .HasColumnType("datetime")
                .HasColumnName("assigned_date");
            entity.Property(e => e.CompletionStatus)
                .HasMaxLength(20)
                .HasDefaultValue("Active")
                .HasColumnName("completion_status");

            entity.HasOne(d => d.User).WithMany()
                .HasForeignKey(d => d.UserId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK__UserChall__user___5CD6CB2B");

            entity.HasOne(d => d.Challenge).WithMany()
                .HasForeignKey(d => d.ChallengeId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK__UserChall__chall___5DCAEF64");
        });

        OnModelCreatingPartial(modelBuilder);
    }

    partial void OnModelCreatingPartial(ModelBuilder modelBuilder);
}
