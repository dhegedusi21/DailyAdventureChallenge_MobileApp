using DAC_API.Models;
using DAC_API.Models.DTO;
using DAC_API.Models.DTO.Challenge;
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

        // Gets a random challenge
        [HttpGet("random")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<ChallengeResponseDTO>> GetRandomChallenge() {
            try {
                var challengeCount = await _context.Challenges.CountAsync();

                if (challengeCount == 0) {
                    return NotFound("No challenges found in the database");
                }

                var random = new Random();
                var randomSkip = random.Next(0, challengeCount);

                var randomChallenge = await _context.Challenges
                    .Skip(randomSkip)
                    .FirstOrDefaultAsync();

                if (randomChallenge == null) {
                    return NotFound("Failed to retrieve a random challenge");
                }

                var challengeDto = new ChallengeResponseDTO {
                    IdChallenge = randomChallenge.IdChallenge,
                    Description = randomChallenge.Description,
                    Difficulty = randomChallenge.Difficulty,
                    Points = randomChallenge.Points
                };

                return Ok(challengeDto);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error retrieving data from the database: " + ex.Message);
            }
        }
    }
}
