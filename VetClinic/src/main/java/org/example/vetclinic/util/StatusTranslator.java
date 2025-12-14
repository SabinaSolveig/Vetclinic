package org.example.vetclinic.util;
public class StatusTranslator {
    public static String translateAppointmentStatus(String status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case "Scheduled":
                return "Запланировано";
            case "Completed":
                return "Завершено";
            case "Cancelled":
                return "Отменено";
            case "NoShow":
                return "Не явился";
            default:
                return status;
        }
    }
    public static String translateAppointmentStatusFromRussian(String russianStatus) {
        if (russianStatus == null) {
            return "Scheduled";
        }
        switch (russianStatus) {
            case "Запланировано":
                return "Scheduled";
            case "Завершено":
                return "Completed";
            case "Отменено":
                return "Cancelled";
            case "Не явился":
                return "NoShow";
            default:
                return russianStatus;
        }
    }
    public static String translatePaymentStatus(String status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case "Pending":
                return "Ожидает";
            case "Completed":
                return "Завершено";
            case "Cancelled":
                return "Отменено";
            case "Refunded":
                return "Возвращено";
            default:
                return status;
        }
    }
    public static String translatePaymentStatusFromRussian(String russianStatus) {
        if (russianStatus == null) {
            return "Pending";
        }
        switch (russianStatus) {
            case "Ожидает":
                return "Pending";
            case "Завершено":
                return "Completed";
            case "Отменено":
                return "Cancelled";
            case "Возвращено":
                return "Refunded";
            default:
                return russianStatus;
        }
    }
}
