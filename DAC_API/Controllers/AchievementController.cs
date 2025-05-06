using DAC_API.Models;
using DAC_API.Models.DTO.Achievement;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace DAC_API.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class AchievementController : ControllerBase
    {
        private readonly DailyAdventureAppContext _context;

        public AchievementController(DailyAdventureAppContext context)
        {
            _context = context;
        }

        // Gets all achievements
        [HttpGet]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<IEnumerable<AchievementResponseDTO>>> GetAllAchievements()
        {
            try
            {
                var achievements = await _context.Achievements.ToListAsync();
                var achievementDtos = achievements.Select(a => new AchievementResponseDTO
                {
                    IdAchievement = a.IdAchievement,
                    Name = a.Name,
                    Requirements = a.Requirements
                }).ToList();

                return Ok(achievementDtos);
            }
            catch (Exception ex)
            {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error retrieving data from the database: " + ex.Message);
            }
        }

        // Gets an achievement by ID
        [HttpGet("{id}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<AchievementResponseDTO>> GetAchievementById(int id)
        {
            try
            {
                var achievement = await _context.Achievements.FindAsync(id);

                if (achievement == null)
                {
                    return NotFound($"Achievement with ID {id} not found");
                }

                var achievementDto = new AchievementResponseDTO
                {
                    IdAchievement = achievement.IdAchievement,
                    Name = achievement.Name,
                    Requirements = achievement.Requirements
                };

                return Ok(achievementDto);
            }
            catch (Exception ex)
            {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error retrieving data from the database: " + ex.Message);
            }
        }
    }
}