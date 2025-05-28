using DAC_API.Models;
using DAC_API.Models.DTO;
using DAC_API.Models.DTO.Submission;
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
    public class SubmissionController : ControllerBase {
        private readonly DailyAdventureAppContext _context;

        public SubmissionController(DailyAdventureAppContext context) {
            _context = context;
        }

        // Gets all submissions
        [HttpGet]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<IEnumerable<SubmissionResponseDTO>>> GetAllSubmissions() {
            try {
                var submissions = await _context.Submissions
                    .Include(s => s.User)
                    .Include(s => s.Challenge)
                    .Include(s => s.Votes)
                    .ToListAsync();

                var submissionDtos = submissions.Select(s => new SubmissionResponseDTO {
                    IdSubmission = s.IdSubmission,
                    UserId = s.UserId,
                    ChallengeId = s.ChallengeId,
                    PhotoUrl = s.PhotoUrl,
                    Status = s.Status ?? "Pending",
                    CreatedAt = s.CreatedAt,
                    Username = s.User.Username,
                    ChallengeDescription = s.Challenge.Description ?? "No description",
                    PositiveVotes = s.Votes.Count(v => v.VoteStatus == "Positive"),
                    NegativeVotes = s.Votes.Count(v => v.VoteStatus == "Negative")

                }).ToList();

                return Ok(submissionDtos);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error retrieving submissions: " + ex.Message);
            }
        }

        // Gets a submission by ID
        [HttpGet("{id}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<SubmissionResponseDTO>> GetSubmissionById(int id) {
            try {
                var submission = await _context.Submissions
                    .Include(s => s.User)
                    .Include(s => s.Challenge)
                    .Include(s => s.Votes)
                    .FirstOrDefaultAsync(s => s.IdSubmission == id);

                if (submission == null) {
                    return NotFound($"Submission with ID {id} not found");
                }

                var submissionDto = new SubmissionResponseDTO {
                    IdSubmission = submission.IdSubmission,
                    UserId = submission.UserId,
                    ChallengeId = submission.ChallengeId,
                    PhotoUrl = submission.PhotoUrl,
                    Status = submission.Status ?? "Pending",
                    CreatedAt = submission.CreatedAt,
                    Username = submission.User.Username,
                    ChallengeDescription = submission.Challenge.Description ?? "No description",
                    PositiveVotes = submission.Votes.Count(v => v.VoteStatus == "Positive"),
                    NegativeVotes = submission.Votes.Count(v => v.VoteStatus == "Negative")
                };

                return Ok(submissionDto);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error retrieving submission: " + ex.Message);
            }
        }

        // Gets all submissions for a specific user
        [HttpGet("user/{userId}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<IEnumerable<SubmissionResponseDTO>>> GetSubmissionsByUser(int userId) {
            try {
                var userExists = await _context.Users.AnyAsync(u => u.IdUser == userId);
                if (!userExists) {
                    return NotFound($"User with ID {userId} not found");
                }

                var submissions = await _context.Submissions
                    .Include(s => s.User)
                    .Include(s => s.Challenge)
                    .Include(s => s.Votes)
                    .Where(s => s.UserId == userId)
                    .OrderByDescending(s => s.CreatedAt)
                    .ToListAsync();

                var submissionDtos = submissions.Select(s => new SubmissionResponseDTO {
                    IdSubmission = s.IdSubmission,
                    UserId = s.UserId,
                    ChallengeId = s.ChallengeId,
                    PhotoUrl = s.PhotoUrl,
                    Status = s.Status ?? "Pending",
                    CreatedAt = s.CreatedAt,
                    Username = s.User.Username,
                    ChallengeDescription = s.Challenge.Description ?? "No description",
                    PositiveVotes = s.Votes.Count(v => v.VoteStatus == "Positive"),
                    NegativeVotes = s.Votes.Count(v => v.VoteStatus == "Negative")

                }).ToList();

                return Ok(submissionDtos);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error retrieving submissions: " + ex.Message);
            }
        }

        // Gets all submissions for a specific challenge
        [HttpGet("challenge/{challengeId}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<IEnumerable<SubmissionResponseDTO>>> GetSubmissionsByChallenge(int challengeId) {
            try {
                var challengeExists = await _context.Challenges.AnyAsync(c => c.IdChallenge == challengeId);
                if (!challengeExists) {
                    return NotFound($"Challenge with ID {challengeId} not found");
                }

                var submissions = await _context.Submissions
                    .Include(s => s.User)
                    .Include(s => s.Challenge)
                    .Include(s => s.Votes)
                    .Where(s => s.ChallengeId == challengeId)
                    .OrderByDescending(s => s.CreatedAt)
                    .ToListAsync();

                var submissionDtos = submissions.Select(s => new SubmissionResponseDTO {
                    IdSubmission = s.IdSubmission,
                    UserId = s.UserId,
                    ChallengeId = s.ChallengeId,
                    PhotoUrl = s.PhotoUrl,
                    Status = s.Status ?? "Pending",
                    CreatedAt = s.CreatedAt,
                    Username = s.User.Username,
                    ChallengeDescription = s.Challenge.Description ?? "No description",
                    PositiveVotes = s.Votes.Count(v => v.VoteStatus == "Positive"),
                    NegativeVotes = s.Votes.Count(v => v.VoteStatus == "Negative")

                }).ToList();

                return Ok(submissionDtos);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error retrieving submissions: " + ex.Message);
            }
        }

        // Creates a new submission
        [Authorize]
        [HttpPost]
        [ProducesResponseType(StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<SubmissionResponseDTO>> CreateSubmission(CreateSubmissionDTO createSubmissionDto) {
            if (!ModelState.IsValid) {
                return BadRequest(ModelState);
            }

            try {
                var userExists = await _context.Users.AnyAsync(u => u.IdUser == createSubmissionDto.UserId);
                if (!userExists) {
                    return BadRequest($"User with ID {createSubmissionDto.UserId} not found");
                }

                var challengeExists = await _context.Challenges.AnyAsync(c => c.IdChallenge == createSubmissionDto.ChallengeId);
                if (!challengeExists) {
                    return BadRequest($"Challenge with ID {createSubmissionDto.ChallengeId} not found");
                }

                var existingSubmission = await _context.Submissions
                    .AnyAsync(s => s.UserId == createSubmissionDto.UserId &&
                                  s.ChallengeId == createSubmissionDto.ChallengeId);

                if (existingSubmission) {
                    return BadRequest("You have already submitted for this challenge");
                }

                var submission = new Submission {
                    UserId = createSubmissionDto.UserId,
                    ChallengeId = createSubmissionDto.ChallengeId,
                    PhotoUrl = createSubmissionDto.PhotoUrl,
                    Status = "Pending",
                    CreatedAt = DateTime.Now
                };

                _context.Submissions.Add(submission);

                // Update UserChallenge status to "Completed"
                var userChallenge = await _context.UserChallenges
                    .FirstOrDefaultAsync(uc => uc.UserId == createSubmissionDto.UserId &&
                                              uc.ChallengeId == createSubmissionDto.ChallengeId &&
                                              uc.CompletionStatus == "Active");

                if (userChallenge != null) {
                    userChallenge.CompletionStatus = "Completed";
                    _context.Entry(userChallenge).State = EntityState.Modified;
                }

                await _context.SaveChangesAsync();

                var createdSubmission = await _context.Submissions
                    .Include(s => s.User)
                    .Include(s => s.Challenge)
                    .FirstOrDefaultAsync(s => s.IdSubmission == submission.IdSubmission);

                var submissionResponseDto = new SubmissionResponseDTO {
                    IdSubmission = createdSubmission.IdSubmission,
                    UserId = createdSubmission.UserId,
                    ChallengeId = createdSubmission.ChallengeId,
                    PhotoUrl = createdSubmission.PhotoUrl,
                    Status = createdSubmission.Status,
                    CreatedAt = createdSubmission.CreatedAt,
                    Username = createdSubmission.User.Username,
                    ChallengeDescription = createdSubmission.Challenge.Description ?? "No description",
                    PositiveVotes = 0,
                    NegativeVotes = 0

                };

                return CreatedAtAction(nameof(GetSubmissionById),
                    new { id = submission.IdSubmission },
                    submissionResponseDto);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error creating submission: " + ex.Message);
            }
        }

        // Deletes a submission
        [Authorize]
        [HttpDelete("{id}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<IActionResult> DeleteSubmission(int id) {
            try {
                var submission = await _context.Submissions.FindAsync(id);

                if (submission == null) {
                    return NotFound($"Submission with ID {id} not found");
                }

                var hasVotes = await _context.Votes.AnyAsync(v => v.SubmissionId == id);
                if (hasVotes) {
                    var votes = await _context.Votes.Where(v => v.SubmissionId == id).ToListAsync();
                    _context.Votes.RemoveRange(votes);
                }

                _context.Submissions.Remove(submission);
                await _context.SaveChangesAsync();

                return NoContent();
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error deleting submission: " + ex.Message);
            }
        }
    }
}
