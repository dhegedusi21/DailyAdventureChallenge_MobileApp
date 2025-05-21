using DAC_API.Models;
using DAC_API.Models.DTO;
using DAC_API.Models.DTO.Vote;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace DAC_API.Controllers {
    [ApiController]
    [Route("api/[controller]")]
    public class VoteController : ControllerBase {
        private readonly DailyAdventureAppContext _context;

        public VoteController(DailyAdventureAppContext context) {
            _context = context;
        }

        // Gets all votes
        [HttpGet]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<IEnumerable<VoteResponseDTO>>> GetAllVotes() {
            try {
                var votes = await _context.Votes
                    .Include(v => v.User)
                    .Include(v => v.Submission)
                    .ToListAsync();

                var voteDtos = votes.Select(v => new VoteResponseDTO {
                    IdVote = v.IdVote,
                    SubmissionId = v.SubmissionId,
                    UserId = v.UserId,
                    VoteStatus = v.VoteStatus,
                    CreatedAt = v.CreatedAt,
                    Username = v.User.Username,
                    SubmissionPhotoUrl = v.Submission.PhotoUrl
                }).ToList();

                return Ok(voteDtos);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error retrieving votes: " + ex.Message);
            }
        }

        // Gets a vote by ID
        [HttpGet("{id}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<VoteResponseDTO>> GetVoteById(int id) {
            try {
                var vote = await _context.Votes
                    .Include(v => v.User)
                    .Include(v => v.Submission)
                    .FirstOrDefaultAsync(v => v.IdVote == id);

                if (vote == null) {
                    return NotFound($"Vote with ID {id} not found");
                }

                var voteDto = new VoteResponseDTO {
                    IdVote = vote.IdVote,
                    SubmissionId = vote.SubmissionId,
                    UserId = vote.UserId,
                    VoteStatus = vote.VoteStatus,
                    CreatedAt = vote.CreatedAt,
                    Username = vote.User.Username,
                    SubmissionPhotoUrl = vote.Submission.PhotoUrl
                };

                return Ok(voteDto);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error retrieving vote: " + ex.Message);
            }
        }

        // Gets all votes for a specific submission
        [HttpGet("submission/{submissionId}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<IEnumerable<VoteResponseDTO>>> GetVotesBySubmission(int submissionId) {
            try {
                var submissionExists = await _context.Submissions.AnyAsync(s => s.IdSubmission == submissionId);
                if (!submissionExists) {
                    return NotFound($"Submission with ID {submissionId} not found");
                }

                var votes = await _context.Votes
                    .Include(v => v.User)
                    .Include(v => v.Submission)
                    .Where(v => v.SubmissionId == submissionId)
                    .ToListAsync();

                var voteDtos = votes.Select(v => new VoteResponseDTO {
                    IdVote = v.IdVote,
                    SubmissionId = v.SubmissionId,
                    UserId = v.UserId,
                    VoteStatus = v.VoteStatus,
                    CreatedAt = v.CreatedAt,
                    Username = v.User.Username,
                    SubmissionPhotoUrl = v.Submission.PhotoUrl
                }).ToList();

                return Ok(voteDtos);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error retrieving votes: " + ex.Message);
            }
        }

        // Gets all votes by a specific user
        [HttpGet("user/{userId}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<IEnumerable<VoteResponseDTO>>> GetVotesByUser(int userId) {
            try {
                var userExists = await _context.Users.AnyAsync(u => u.IdUser == userId);
                if (!userExists) {
                    return NotFound($"User with ID {userId} not found");
                }

                var votes = await _context.Votes
                    .Include(v => v.User)
                    .Include(v => v.Submission)
                    .Where(v => v.UserId == userId)
                    .ToListAsync();

                var voteDtos = votes.Select(v => new VoteResponseDTO {
                    IdVote = v.IdVote,
                    SubmissionId = v.SubmissionId,
                    UserId = v.UserId,
                    VoteStatus = v.VoteStatus,
                    CreatedAt = v.CreatedAt,
                    Username = v.User.Username,
                    SubmissionPhotoUrl = v.Submission.PhotoUrl
                }).ToList();

                return Ok(voteDtos);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error retrieving votes: " + ex.Message);
            }
        }

        // Gets the count of upvotes and downvotes for a submission
        [HttpGet("submission/{submissionId}/count")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<object>> GetVoteCountForSubmission(int submissionId) {
            try {
                var submissionExists = await _context.Submissions.AnyAsync(s => s.IdSubmission == submissionId);
                if (!submissionExists) {
                    return NotFound($"Submission with ID {submissionId} not found");
                }

                var votes = await _context.Votes
                    .Where(v => v.SubmissionId == submissionId)
                    .ToListAsync();

                int upvotes = votes.Count(v => v.VoteStatus == "Upvote");
                int downvotes = votes.Count(v => v.VoteStatus == "Downvote");
                int total = upvotes - downvotes;

                return Ok(new {
                    SubmissionId = submissionId,
                    Upvotes = upvotes,
                    Downvotes = downvotes,
                    Total = total
                });
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error retrieving vote count: " + ex.Message);
            }
        }

        // Checks if a user has voted on a specific submission
        [HttpGet("user/{userId}/submission/{submissionId}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<VoteResponseDTO>> GetUserVoteForSubmission(int userId, int submissionId) {
            try {
                var vote = await _context.Votes
                    .Include(v => v.User)
                    .Include(v => v.Submission)
                    .FirstOrDefaultAsync(v => v.UserId == userId && v.SubmissionId == submissionId);

                if (vote == null) {
                    return NotFound($"No vote found for user {userId} on submission {submissionId}");
                }

                var voteDto = new VoteResponseDTO {
                    IdVote = vote.IdVote,
                    SubmissionId = vote.SubmissionId,
                    UserId = vote.UserId,
                    VoteStatus = vote.VoteStatus,
                    CreatedAt = vote.CreatedAt,
                    Username = vote.User.Username,
                    SubmissionPhotoUrl = vote.Submission.PhotoUrl
                };

                return Ok(voteDto);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error retrieving vote: " + ex.Message);
            }
        }

        // Creates a new vote or updates an existing one
        [HttpPost]
        [ProducesResponseType(StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<VoteResponseDTO>> CreateOrUpdateVote(CreateVoteDTO createVoteDto) {
            if (!ModelState.IsValid) {
                return BadRequest(ModelState);
            }

            try {
                var user = await _context.Users.FindAsync(createVoteDto.UserId);
                if (user == null) {
                    return BadRequest($"User with ID {createVoteDto.UserId} not found");
                }

                var submission = await _context.Submissions
                    .Include(s => s.User)
                    .FirstOrDefaultAsync(s => s.IdSubmission == createVoteDto.SubmissionId);

                if (submission == null) {
                    return BadRequest($"Submission with ID {createVoteDto.SubmissionId} not found");
                }

                if (submission.UserId == createVoteDto.UserId) {
                    return BadRequest("You cannot vote on your own submission");
                }

                var existingVote = await _context.Votes
                    .FirstOrDefaultAsync(v => v.UserId == createVoteDto.UserId &&
                                             v.SubmissionId == createVoteDto.SubmissionId);

                if (existingVote != null) {
                    if (existingVote.VoteStatus != createVoteDto.VoteStatus) {
                        existingVote.VoteStatus = createVoteDto.VoteStatus;
                        existingVote.CreatedAt = DateTime.Now;

                        _context.Entry(existingVote).State = EntityState.Modified;
                        await _context.SaveChangesAsync();

                        var updatedVoteDto = new VoteResponseDTO {
                            IdVote = existingVote.IdVote,
                            SubmissionId = existingVote.SubmissionId,
                            UserId = existingVote.UserId,
                            VoteStatus = existingVote.VoteStatus,
                            CreatedAt = existingVote.CreatedAt,
                            Username = user.Username,
                            SubmissionPhotoUrl = submission.PhotoUrl
                        };

                        return Ok(updatedVoteDto);
                    } else {
                        var existingVoteDto = new VoteResponseDTO {
                            IdVote = existingVote.IdVote,
                            SubmissionId = existingVote.SubmissionId,
                            UserId = existingVote.UserId,
                            VoteStatus = existingVote.VoteStatus,
                            CreatedAt = existingVote.CreatedAt,
                            Username = user.Username,
                            SubmissionPhotoUrl = submission.PhotoUrl
                        };

                        return Ok(existingVoteDto);
                    }
                }

                var vote = new Vote {
                    SubmissionId = createVoteDto.SubmissionId,
                    UserId = createVoteDto.UserId,
                    VoteStatus = createVoteDto.VoteStatus,
                    CreatedAt = DateTime.Now
                };

                _context.Votes.Add(vote);
                await _context.SaveChangesAsync();

                var notification = new Notification {
                    UserId = submission.UserId,
                    Name = "New Vote",
                    Message = $"{user.Username} {createVoteDto.VoteStatus.ToLower()}d your submission",
                    Type = "Vote",
                    IsRead = false,
                    CreatedAt = DateTime.Now
                };

                _context.Notifications.Add(notification);
                await _context.SaveChangesAsync();

                var voteResponseDto = new VoteResponseDTO {
                    IdVote = vote.IdVote,
                    SubmissionId = vote.SubmissionId,
                    UserId = vote.UserId,
                    VoteStatus = vote.VoteStatus,
                    CreatedAt = vote.CreatedAt,
                    Username = user.Username,
                    SubmissionPhotoUrl = submission.PhotoUrl
                };

                return CreatedAtAction(
                    nameof(GetVoteById),
                    new { id = vote.IdVote },
                    voteResponseDto);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error creating vote: " + ex.Message);
            }
        }

        // Deletes a vote
        [HttpDelete("{id}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<IActionResult> DeleteVote(int id) {
            try {
                var vote = await _context.Votes.FindAsync(id);

                if (vote == null) {
                    return NotFound($"Vote with ID {id} not found");
                }

                _context.Votes.Remove(vote);
                await _context.SaveChangesAsync();

                return NoContent();
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error deleting vote: " + ex.Message);
            }
        }

        // Helper methods
        private bool VoteExists(int id) {
            return _context.Votes.Any(e => e.IdVote == id);
        }
    }
}

