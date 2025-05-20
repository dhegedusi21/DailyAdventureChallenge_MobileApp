using DAC_API.Models;
using DAC_API.Models.DTO;
using DAC_API.Models.DTO.UserAchievement;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace DAC_API.Controllers {
    [ApiController]
    [Route("api/[controller]")]
    public class UserAchievementController : ControllerBase {
        private readonly DailyAdventureAppContext _context;

        public UserAchievementController(DailyAdventureAppContext context) {
            _context = context;
        }

        // Gets all user achievements
        [HttpGet]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<IEnumerable<UserAchievementResponseDTO>>> GetAllUserAchievements() {
            try {
                var userAchievements = await _context.UserAchievements
                    .Include(ua => ua.User)
                    .Include(ua => ua.Achievement)
                    .ToListAsync();

                var userAchievementDtos = userAchievements.Select(ua => new UserAchievementResponseDTO {
                    UserId = ua.UserId,
                    AchievementId = ua.AchievementId,
                    EarnedAt = ua.EarnedAt,
                    Username = ua.User.Username,
                    AchievementName = ua.Achievement.Name,
                    AchievementRequirements = ua.Achievement.Requirements
                }).ToList();

                return Ok(userAchievementDtos);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error retrieving user achievements: " + ex.Message);
            }
        }

        // Gets all achievements for a specific user
        [HttpGet("user/{userId}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<IEnumerable<UserAchievementResponseDTO>>> GetUserAchievementsByUser(int userId) {
            try {
                var userExists = await _context.Users.AnyAsync(u => u.IdUser == userId);
                if (!userExists) {
                    return NotFound($"User with ID {userId} not found");
                }

                var userAchievements = await _context.UserAchievements
                    .Include(ua => ua.User)
                    .Include(ua => ua.Achievement)
                    .Where(ua => ua.UserId == userId)
                    .OrderByDescending(ua => ua.EarnedAt)
                    .ToListAsync();

                var userAchievementDtos = userAchievements.Select(ua => new UserAchievementResponseDTO {
                    UserId = ua.UserId,
                    AchievementId = ua.AchievementId,
                    EarnedAt = ua.EarnedAt,
                    Username = ua.User.Username,
                    AchievementName = ua.Achievement.Name,
                    AchievementRequirements = ua.Achievement.Requirements
                }).ToList();

                return Ok(userAchievementDtos);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error retrieving user achievements: " + ex.Message);
            }
        }

        // Gets all users who have earned a specific achievement
        [HttpGet("achievement/{achievementId}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<IEnumerable<UserAchievementResponseDTO>>> GetUserAchievementsByAchievement(int achievementId) {
            try {
                var achievementExists = await _context.Achievements.AnyAsync(a => a.IdAchievement == achievementId);
                if (!achievementExists) {
                    return NotFound($"Achievement with ID {achievementId} not found");
                }

                var userAchievements = await _context.UserAchievements
                    .Include(ua => ua.User)
                    .Include(ua => ua.Achievement)
                    .Where(ua => ua.AchievementId == achievementId)
                    .OrderByDescending(ua => ua.EarnedAt)
                    .ToListAsync();

                var userAchievementDtos = userAchievements.Select(ua => new UserAchievementResponseDTO {
                    UserId = ua.UserId,
                    AchievementId = ua.AchievementId,
                    EarnedAt = ua.EarnedAt,
                    Username = ua.User.Username,
                    AchievementName = ua.Achievement.Name,
                    AchievementRequirements = ua.Achievement.Requirements
                }).ToList();

                return Ok(userAchievementDtos);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error retrieving user achievements: " + ex.Message);
            }
        }

        // Checks if a specific user has earned a specific achievement
        [HttpGet("user/{userId}/achievement/{achievementId}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<UserAchievementResponseDTO>> GetSpecificUserAchievement(int userId, int achievementId) {
            try {
                var userAchievement = await _context.UserAchievements
                    .Include(ua => ua.User)
                    .Include(ua => ua.Achievement)
                    .FirstOrDefaultAsync(ua => ua.UserId == userId && ua.AchievementId == achievementId);

                if (userAchievement == null) {
                    return NotFound($"User {userId} has not earned achievement {achievementId}");
                }

                var userAchievementDto = new UserAchievementResponseDTO {
                    UserId = userAchievement.UserId,
                    AchievementId = userAchievement.AchievementId,
                    EarnedAt = userAchievement.EarnedAt,
                    Username = userAchievement.User.Username,
                    AchievementName = userAchievement.Achievement.Name,
                    AchievementRequirements = userAchievement.Achievement.Requirements
                };

                return Ok(userAchievementDto);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error retrieving user achievement: " + ex.Message);
            }
        }

        // Awards an achievement to a user
        [HttpPost]
        [ProducesResponseType(StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<UserAchievementResponseDTO>> AwardAchievement(CreateUserAchievementDTO createUserAchievementDto) {
            if (!ModelState.IsValid) {
                return BadRequest(ModelState);
            }

            try {
                var user = await _context.Users.FindAsync(createUserAchievementDto.UserId);
                if (user == null) {
                    return BadRequest($"User with ID {createUserAchievementDto.UserId} not found");
                }

                var achievement = await _context.Achievements.FindAsync(createUserAchievementDto.AchievementId);
                if (achievement == null) {
                    return BadRequest($"Achievement with ID {createUserAchievementDto.AchievementId} not found");
                }

                var existingUserAchievement = await _context.UserAchievements
                    .FirstOrDefaultAsync(ua => ua.UserId == createUserAchievementDto.UserId &&
                                              ua.AchievementId == createUserAchievementDto.AchievementId);

                if (existingUserAchievement != null) {
                    return BadRequest($"User {user.Username} already has the achievement '{achievement.Name}'");
                }

                var userAchievement = new UserAchievement {
                    UserId = createUserAchievementDto.UserId,
                    AchievementId = createUserAchievementDto.AchievementId,
                    EarnedAt = DateTime.Now
                };

                _context.UserAchievements.Add(userAchievement);
                await _context.SaveChangesAsync();

                var notification = new Notification {
                    UserId = userAchievement.UserId,
                    Name = "Achievement Earned",
                    Message = $"Congratulations! You've earned the achievement: {achievement.Name}",
                    Type = "Achievement",
                    IsRead = false,
                    CreatedAt = DateTime.Now
                };

                _context.Notifications.Add(notification);
                await _context.SaveChangesAsync();

                var userAchievementDto = new UserAchievementResponseDTO {
                    UserId = userAchievement.UserId,
                    AchievementId = userAchievement.AchievementId,
                    EarnedAt = userAchievement.EarnedAt,
                    Username = user.Username,
                    AchievementName = achievement.Name,
                    AchievementRequirements = achievement.Requirements
                };

                return CreatedAtAction(
                    nameof(GetSpecificUserAchievement),
                    new { userId = userAchievement.UserId, achievementId = userAchievement.AchievementId },
                    userAchievementDto);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error awarding achievement: " + ex.Message);
            }
        }
    }
}
