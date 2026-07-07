package com.petspa.app.network

import com.petspa.app.model.*
import com.petspa.app.network.dto.request.BookingRequest
import com.petspa.app.network.dto.request.PaymentCreateRequest
import com.petspa.app.network.dto.request.PaymentRequest
import com.petspa.app.network.dto.request.UpdateUserRequest
import com.petspa.app.network.dto.response.*
import okhttp3.MultipartBody
import retrofit2.http.*

// API Retrofit - Kết nối Backend thật
interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @GET("pets")
    suspend fun getPets(): List<PetResponse>

    @POST("pets")
    suspend fun addPet(@Body pet: Pet): PetResponse

    @PUT("pets/{id}")
    suspend fun updatePet(@Path("id") id: String, @Body pet: Pet): PetResponse

    @DELETE("pets/{id}")
    suspend fun deletePet(@Path("id") id: String)

    @GET("services")
    suspend fun getServices(): List<ServiceResponse>

    @GET("bookings")
    suspend fun getCustomerBookings(): List<BookingResponse>

    @POST("bookings")
    suspend fun createBooking(@Body booking: BookingRequest): BookingResponse

    @POST("payments/create")
    suspend fun createPayment(@Body request: PaymentCreateRequest): PaymentResponseDto

    @GET("payments/{bookingId}/status")
    suspend fun getPaymentStatus(@Path("bookingId") bookingId: Long): PaymentTransactionDto

    @Multipart
    @POST("images/upload")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Query("type") type: String
    ): Map<String, String>

    @PUT("bookings/{id}/payment")
    suspend fun updatePayment(@Path("id") id: String, @Body payment: PaymentRequest): BookingResponse

    @DELETE("bookings/{id}")
    suspend fun cancelBooking(@Path("id") id: String)

    @PUT("bookings/{id}")
    suspend fun rescheduleBooking(@Path("id") id: String, @Body data: Map<String, String>): BookingResponse

    @GET("notifications")
    suspend fun getCustomerNotifications(): List<NotificationResponse>

    @GET("notif-settings")
    suspend fun getNotifSettings(): NotifSettings

    @PUT("notif-settings")
    suspend fun updateNotifSettings(@Body settings: NotifSettings): NotifSettings

    @GET("user/me")
    suspend fun getCustomerUser(): User

    @POST("users/fcm-token")
    suspend fun updateFcmToken(@Body request: Map<String, String>): Unit

    @PUT("user/me")
    suspend fun updateCustomerUser(@Body request: UpdateUserRequest): User

    @GET("bookings")
    suspend fun getStaffBookings(): List<BookingResponse>

    @GET("shifts")
    suspend fun getShifts(): List<Shift>

    @GET("notifications")
    suspend fun getStaffNotifications(): List<NotificationResponse>

    @GET("staff/me")
    suspend fun getTechnicianProfile(): TechnicianProfile

    @PUT("staff/me")
    suspend fun updateTechnicianProfile(@Body profile: TechnicianProfile): TechnicianProfile

    @GET("customers")
    suspend fun getCustomers(): List<Customer>

    @POST("customers")
    suspend fun addCustomer(@Body customer: Customer): Customer

    @PUT("customers/{id}")
    suspend fun updateCustomer(@Path("id") id: String, @Body customer: Customer): Customer

    @DELETE("customers/{id}")
    suspend fun deleteCustomer(@Path("id") id: String)

    @GET("staff")
    suspend fun getStaffList(): List<StaffMember>

    @GET("staff/available")
    suspend fun getAvailableStaff(@Query("date") date: String): List<StaffAvailabilityResponse>

    @POST("staff")
    suspend fun addStaff(@Body staff: StaffMember): StaffMember

    @PUT("staff/{id}")
    suspend fun updateStaff(@Path("id") id: String, @Body staff: StaffMember): StaffMember

    @DELETE("staff/{id}")
    suspend fun deleteStaff(@Path("id") id: String)

    @GET("services")
    suspend fun getOwnerServices(): List<ServiceResponse>

    @POST("services")
    suspend fun addService(@Body service: Service): ServiceResponse

    @PUT("services/{id}")
    suspend fun updateService(@Path("id") id: String, @Body service: Service): ServiceResponse

    @DELETE("services/{id}")
    suspend fun deleteService(@Path("id") id: String)

    @GET("bookings")
    suspend fun getOwnerBookings(): List<BookingResponse>

    @GET("staff-requests")
    suspend fun getStaffRequests(): List<StaffRequest>

    @GET("reports/revenue/daily")
    suspend fun getDailyRevenue(): List<RevenuePoint>

    @GET("reports/revenue/monthly")
    suspend fun getMonthlyRevenue(): List<RevenuePoint>

    @GET("reports/top-services")
    suspend fun getTopServices(): List<TopService>

    @PATCH("staff-requests/{id}")
    suspend fun updateStaffRequestStatus(@Path("id") id: String, @Body status: Map<String, String>): StaffRequest
}

data class LoginRequest(val email: String, val password: String)
