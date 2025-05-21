using DAC_API.Models;
using DAC_API.Models.DTO;
using DAC_API.Models.DTO.Notification;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace DAC_API.Controllers {
    [Authorize]
    [ApiController]
    [Route("api/[controller]")]
    public class NotificationController : ControllerBase {
        private readonly DailyAdventureAppContext _context;

        public NotificationController(DailyAdventureAppContext context) {
            _context = context;
        }

        // Gets all notifications for a specific user
        [HttpGet("user/{userId}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<IEnumerable<NotificationResponseDTO>>> GetUserNotifications(int userId) {
            try {
                var userExists = await _context.Users.AnyAsync(u => u.IdUser == userId);
                if (!userExists) {
                    return NotFound($"User with ID {userId} not found");
                }

                var notifications = await _context.Notifications
                    .Where(n => n.UserId == userId)
                    .OrderByDescending(n => n.CreatedAt)
                    .ToListAsync();

                var notificationDtos = notifications.Select(n => new NotificationResponseDTO {
                    IdNotification = n.IdNotification,
                    UserId = n.UserId,
                    Name = n.Name,
                    Message = n.Message,
                    Type = n.Type,
                    IsRead = n.IsRead ?? false,
                    CreatedAt = n.CreatedAt
                }).ToList();

                return Ok(notificationDtos);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error retrieving notifications: " + ex.Message);
            }
        }

        // Marks a notification as read
        [HttpPut("{id}/read")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<IActionResult> MarkNotificationAsRead(int id) {
            try {
                var notification = await _context.Notifications.FindAsync(id);

                if (notification == null) {
                    return NotFound($"Notification with ID {id} not found");
                }

                notification.IsRead = true;
                _context.Entry(notification).State = EntityState.Modified;
                await _context.SaveChangesAsync();

                return Ok(new { message = "Notification marked as read" });
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error updating notification: " + ex.Message);
            }
        }

        // Creates a new notification
        [HttpPost]
        [ProducesResponseType(StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<NotificationResponseDTO>> CreateNotification(CreateNotificationDTO createNotificationDto) {
            if (!ModelState.IsValid) {
                return BadRequest(ModelState);
            }

            try {
                var userExists = await _context.Users.AnyAsync(u => u.IdUser == createNotificationDto.UserId);
                if (!userExists) {
                    return BadRequest($"User with ID {createNotificationDto.UserId} not found");
                }

                var notification = new Notification {
                    UserId = createNotificationDto.UserId,
                    Name = createNotificationDto.Name,
                    Message = createNotificationDto.Message,
                    Type = createNotificationDto.Type,
                    IsRead = false,
                    CreatedAt = DateTime.Now
                };

                _context.Notifications.Add(notification);
                await _context.SaveChangesAsync();

                var notificationResponseDto = new NotificationResponseDTO {
                    IdNotification = notification.IdNotification,
                    UserId = notification.UserId,
                    Name = notification.Name,
                    Message = notification.Message,
                    Type = notification.Type,
                    IsRead = notification.IsRead ?? false,
                    CreatedAt = notification.CreatedAt
                };

                return CreatedAtAction(nameof(GetNotification), new { id = notification.IdNotification }, notificationResponseDto);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error creating notification: " + ex.Message);
            }
        }

        // Helper method to get a single notification by ID
        [HttpGet("{id}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<NotificationResponseDTO>> GetNotification(int id) {
            try {
                var notification = await _context.Notifications.FindAsync(id);

                if (notification == null) {
                    return NotFound($"Notification with ID {id} not found");
                }

                var notificationDto = new NotificationResponseDTO {
                    IdNotification = notification.IdNotification,
                    UserId = notification.UserId,
                    Name = notification.Name,
                    Message = notification.Message,
                    Type = notification.Type,
                    IsRead = notification.IsRead ?? false,
                    CreatedAt = notification.CreatedAt
                };

                return Ok(notificationDto);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status500InternalServerError,
                    "Error retrieving notification: " + ex.Message);
            }
        }
    }
}
