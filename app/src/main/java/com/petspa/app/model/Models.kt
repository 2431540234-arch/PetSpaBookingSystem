package com.petspa.app.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class UserRole { CUSTOMER, STAFF, OWNER }

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val gender: String = "",
    val dob: String = "",
    val address: String = "",
    val role: UserRole = UserRole.CUSTOMER
)

data class AuthResponse(
    val token: String,
    val user: User
)

data class Pet(
    val id: String,
    val name: String,
    val species: String,
    val breed: String,
    val gender: String = "",
    val age: Int = 0,
    val weight: Double = 0.0,
    val allergies: String = "",
    val medicalHistory: String = "",
    val notes: String = "",
    val emoji: String = "🐾",
    val imageUrl: String? = null,
    val customerId: String = ""
)

data class Service(
    val id: String,
    val name: String,
    val category: String = "",
    val price: Long,
    val duration: Int,
    val emoji: String = "✨",
    val description: String = "",
    val status: String = "active",
    val imageUrl: String = ""
)

data class StaffMember(
    val id: String,
    val name: String,
    val email: String = "",
    val phone: String = "",
    val specialty: String = "",
    val role: String = "",
    val emoji: String = "👤",
    val status: String = "active",
    val completedBookings: Int = 0,
    val customers: Int = 0,
    val createdAt: String = ""
)

data class Customer(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val status: String = "active",
    val createdAt: String = "",
    val gender: String = "",
    val dob: String = "",
    val address: String = "",
    val totalBookings: Int = 0,
    val completedBookings: Int = 0,
    val cancelledBookings: Int = 0,
    val totalSpent: Long = 0
)

data class Booking(
    val id: String,
    val petId: String = "",
    val petName: String = "",
    val serviceId: String = "",
    val serviceName: String = "",
    val staffId: String = "",
    val staffName: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val date: String,
    val time: String,
    val notes: String = "",
    val status: String = "pending",
    val paymentStatus: String = "unpaid",
    val totalAmount: Long = 0,
    val paidAmount: Long = 0,
    val paymentMethod: String = "",
    val transactionId: String = "",
    val createdAt: String = "",
    val serviceDuration: Int = 60,
    val customerRequests: String = ""
)

data class NotificationItem(
    val id: String,
    val type: String,
    val title: String,
    val message: String,
    val time: String,
    val read: Boolean = false,
    val relatedId: String = ""
)

data class NotifSettings(
    val bookingNew: Boolean = true,
    val bookingConfirmed: Boolean = true,
    val bookingRescheduled: Boolean = true,
    val bookingCancelled: Boolean = true,
    val paymentSuccess: Boolean = true,
    val paymentFailed: Boolean = true,
    val serviceInProgress: Boolean = true,
    val serviceCompleted: Boolean = true,
    val channelPush: Boolean = true,
    val channelEmail: Boolean = false
)

data class Shift(
    val id: String,
    val date: String,
    val type: String,
    val startTime: String,
    val endTime: String,
    val status: String = "pending",
    val technicianId: String = ""
)

data class StaffRequest(
    val id: String,
    val staffId: String,
    val staffName: String,
    val type: String,
    val date: String,
    val reason: String,
    val status: String = "pending",
    val createdAt: String = ""
)

@Parcelize
data class BookingDraft(
    val petId: String = "",
    val serviceId: String = "",
    val staffId: String = "",
    val date: String = "",
    val time: String = "",
    val notes: String = "",
    val paymentMethod: String = ""
) : Parcelable

data class RevenuePoint(val label: String, val revenue: Long, val bookings: Int)
data class TopService(val name: String, val count: Int, val revenue: Long)

data class TechnicianProfile(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val position: String,
    val avatar: String,
    val expertise: List<String>,
    val gender: String,
    val birthDate: String,
    val address: String,
    val joinDate: String
)

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
