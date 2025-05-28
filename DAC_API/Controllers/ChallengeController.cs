using DAC_API.Models;
using DAC_API.Models.DTO;
using DAC_API.Models.DTO.Challenge;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace DAC_API.Controllers {
    [ApiController]
    [Route("api/[controller]")]
    public class ChallengeController : ControllerBase {
        private readonly DailyAdventureAppContext _context;

        public ChallengeController(DailyAdventureAppContext context) {
            _context = context;
        }

        // Gets all challenges
        [HttpGet]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<IEnumerable<ChallengeResponseDTO>>> GetAllChallenges() {
            try {
                var challenges = await _context.Challenges.ToListAsync();
                var challengeDtos = challenges.Select(c => new ChallengeResponseDTO {
                    IdChallenge = c.IdChallenge,
                    Description = c.Description,
                    Difficulty = c.Difficulty,
                    Points = c.Points
                }).ToList();

                return Ok(challengeDtos);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error retrieving data from the database: " + ex.Message);
            }
        }

        // Gets a challenge by ID
        [HttpGet("{id}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<ChallengeResponseDTO>> GetChallengeById(int id) {
            try {
                var challenge = await _context.Challenges.FindAsync(id);

                if (challenge == null) {
                    return NotFound($"Challenge with ID {id} not found");
                }

                var challengeDto = new ChallengeResponseDTO {
                    IdChallenge = challenge.IdChallenge,
                    Description = challenge.Description,
                    Difficulty = challenge.Difficulty,
                    Points = challenge.Points
                };

                return Ok(challengeDto);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error retrieving data from the database: " + ex.Message);
            }
        }

        // Gets challenges by difficulty
        [HttpGet("difficulty/{difficulty}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<IEnumerable<ChallengeResponseDTO>>> GetChallengesByDifficulty(string difficulty) {
            try {
                var challenges = await _context.Challenges
                    .Where(c => c.Difficulty == difficulty)
                    .ToListAsync();

                if (!challenges.Any()) {
                    return NotFound($"No challenges found with difficulty level: {difficulty}");
                }

                var challengeDtos = challenges.Select(c => new ChallengeResponseDTO {
                    IdChallenge = c.IdChallenge,
                    Description = c.Description,
                    Difficulty = c.Difficulty,
                    Points = c.Points
                }).ToList();

                return Ok(challengeDtos);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error retrieving data from the database: " + ex.Message);
            }
        }

        // Get user's current challenge
        [HttpGet("user/{userId}/current")]
        [Authorize]
        public async Task<ActionResult<ChallengeResponseDTO>> GetUserCurrentChallenge(int userId) {
            try {
                var today = DateTime.Today;
                var userChallenge = await _context.UserChallenges
                    .Include(uc => uc.Challenge)
                    .Where(uc => uc.UserId == userId &&
                           uc.AssignedDate.Date == today &&
                           uc.CompletionStatus == "Active")
                    .FirstOrDefaultAsync();

                if (userChallenge == null) {
                    return NotFound("No active challenge found for today");
                }

                var challengeDto = new ChallengeResponseDTO {
                    IdChallenge = userChallenge.Challenge.IdChallenge,
                    Description = userChallenge.Challenge.Description,
                    Difficulty = userChallenge.Challenge.Difficulty,
                    Points = userChallenge.Challenge.Points
                };

                return Ok(challengeDto);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error retrieving current challenge: " + ex.Message);
            }
        }

        // Assign a daily challenge to a user
        [HttpPost("assign")]
        [Authorize]
        public async Task<ActionResult<ChallengeResponseDTO>> AssignDailyChallenge(int userId) {
            try {
                var user = await _context.Users.FindAsync(userId);
                if (user == null) {
                    return NotFound($"User with ID {userId} not found");
                }

                var today = DateTime.Today;

                var oldActiveChallenges = await _context.UserChallenges
                 .Where(uc => uc.UserId == userId &&
                        uc.CompletionStatus == "Active" &&
                        uc.AssignedDate.Date < today)
                 .ToListAsync();

                foreach (var oldChallenge in oldActiveChallenges) {
                    oldChallenge.CompletionStatus = "Expired";
                }

                var existingAssignment = await _context.UserChallenges
                    .Where(uc => uc.UserId == userId &&
                           uc.AssignedDate.Date == today &&
                           uc.CompletionStatus == "Active")
                    .FirstOrDefaultAsync();

                if (existingAssignment != null) {
                    var existingChallenge = await _context.Challenges.FindAsync(existingAssignment.ChallengeId);

                    var existingChallengeDto = new ChallengeResponseDTO {
                        IdChallenge = existingChallenge.IdChallenge,
                        Description = existingChallenge.Description,
                        Difficulty = existingChallenge.Difficulty,
                        Points = existingChallenge.Points
                    };

                    return Ok(new {
                        message = "User already has an active challenge for today",
                        challenge = existingChallengeDto
                    });
                }

                var seed = today.Year * 10000 + today.Month * 100 + today.Day;
                var random = new Random(seed);
                var challengeCount = await _context.Challenges.CountAsync();

                if (challengeCount == 0) {
                    return NotFound("No challenges available");
                }

                var skip = random.Next(0, challengeCount);
                var challenge = await _context.Challenges.Skip(skip).FirstOrDefaultAsync();

                var userChallenge = new UserChallenge {
                    UserId = userId,
                    ChallengeId = challenge.IdChallenge,
                    AssignedDate = DateTime.Now,
                    CompletionStatus = "Active"
                };

                _context.UserChallenges.Add(userChallenge);
                await _context.SaveChangesAsync();

                var challengeDto = new ChallengeResponseDTO {
                    IdChallenge = challenge.IdChallenge,
                    Description = challenge.Description,
                    Difficulty = challenge.Difficulty,
                    Points = challenge.Points
                };

                return Ok(challengeDto);

            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error assigning challenge: " + ex.Message);
            }
        }
    }
}
