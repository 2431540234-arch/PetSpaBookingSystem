package com.petspa.app.network

import com.petspa.app.model.*
import retrofit2.http.*

// API Retrofit mock - dữ liệu từ Figma mockData
interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @GET("pets")
    suspend fun getPets(): List<Pet>

    @POST("pets")
    suspend fun addPet(@Body pet: Pet): Pet

    @PUT("pets/{id}")
    suspend fun updatePet(@Path("id") id: String, @Body pet: Pet): Pet

    @DELETE("pets/{id}")
    suspend fun deletePet(@Path("id") id: String)

    @GET("services")
    suspend fun getServices(): List<Service>

    @GET("bookings")
    suspend fun getCustomerBookings(): List<Booking>

    @POST("bookings")
    suspend fun createBooking(@Body booking: BookingDraft): Booking

    @DELETE("bookings/{id}")
    suspend fun cancelBooking(@Path("id") id: String)

    @PUT("bookings/{id}")
    suspend fun rescheduleBooking(@Path("id") id: String, @Body data: Map<String, String>): Booking

    @GET("notifications")
    suspend fun getCustomerNotifications(): List<NotificationItem>

    @GET("notif-settings")
    suspend fun getNotifSettings(): NotifSettings

    @PUT("notif-settings")
    suspend fun updateNotifSettings(@Body settings: NotifSettings): NotifSettings

    @GET("user/me")
    suspend fun getCustomerUser(): User

    @GET("bookings")
    suspend fun getStaffBookings(): List<Booking>

    @GET("shifts")
    suspend fun getShifts(): List<Shift>

    @GET("notifications")
    suspend fun getStaffNotifications(): List<NotificationItem>

    @GET("staff/me")
    suspend fun getTechnicianProfile(): TechnicianProfile

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

    @POST("staff")
    suspend fun addStaff(@Body staff: StaffMember): StaffMember

    @PUT("staff/{id}")
    suspend fun updateStaff(@Path("id") id: String, @Body staff: StaffMember): StaffMember

    @DELETE("staff/{id}")
    suspend fun deleteStaff(@Path("id") id: String)

    @GET("services")
    suspend fun getOwnerServices(): List<Service>

    @POST("services")
    suspend fun addService(@Body service: Service): Service

    @PUT("services/{id}")
    suspend fun updateService(@Path("id") id: String, @Body service: Service): Service

    @DELETE("services/{id}")
    suspend fun deleteService(@Path("id") id: String)

    @GET("bookings")
    suspend fun getOwnerBookings(): List<Booking>

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
