package services;

import entitites.Customer;
import entitites.Merchant;
import entitites.Payment;
import repository.CustomerRepo;
import repository.MerchantRepo;
import utils.ReadDataFromFile;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PaymentService {
    public Set<Payment> getPaymentsFromFile() throws SQLException {
        CustomerRepo customerRepo = new CustomerRepo();
        MerchantRepo merchantRepo = new MerchantRepo();
        Set<Payment> paymentsList = new HashSet<>();
        String path = "C:\\Users\\astar\\IdeaProjects\\CashManagement\\payments.dat";
        List<String> paymentsDataList = ReadDataFromFile.getFromFile(path);
        for (String str : paymentsDataList) {
            String[] tempArray = str.split(",");
            Timestamp date = getTimestamp(tempArray[0]);
            if (customerRepo.getByName(tempArray[1]) == null) {
                continue;
            }
            Customer customer = null;
            customer = customerRepo.getByName(tempArray[1]);
            Merchant merchant = null;
            merchant = merchantRepo.getByName(tempArray[2]);
            String productName = tempArray[3];
            double sumPaid = Double.valueOf(tempArray[4]);
            double chargePaid = 0.00;

            paymentsList.add(new Payment(date, merchant, customer, productName, sumPaid, chargePaid));
        }
        return paymentsList;
    }

    public Timestamp getTimestamp(String str) {
        Timestamp date = Timestamp.valueOf(str);
        return date;
    }

    public Payment calculateCharge(Payment p) {
        Payment payment = p;
        double commissionRate = 0.02;
        double chargePaid = p.getSumPaid() * commissionRate;
        payment.setChargePaid(chargePaid);
        return payment;
    }
}