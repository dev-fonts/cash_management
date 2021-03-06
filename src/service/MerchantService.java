package service;

import entity.Merchant;
import entity.Payment;
import repository.CustomerRepo;
import repository.MerchantRepo;
import repository.PaymentRepo;
import utils.DataFileReader;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MerchantService {
    private CustomerRepo customerRepo;
    private MerchantRepo merchantRepo;
    private PaymentRepo paymentRepo;

    public MerchantService() {
        this.customerRepo = new CustomerRepo();
        this.merchantRepo = new MerchantRepo();
        this.paymentRepo = new PaymentRepo();

        this.merchantRepo.setPaymentRepo(paymentRepo);
        this.paymentRepo.setCustomerRepo(customerRepo);
        this.paymentRepo.setMerchantRepo(merchantRepo);

     }

    public List<Merchant> getMerchantsFromFile() {
        String path = "C:\\Users\\astar\\IdeaProjects\\CashManagement\\merchants.dat";
        List<Merchant> merchantsList = new ArrayList<>();
        List<String> merchantsData = DataFileReader.getDataFromFile(path);
        for (String str : merchantsData) {
            String[] tempArray = str.split(",");
            String name = tempArray[0];
            String bankName = tempArray[1];
            String swift = tempArray[2];
            String account = tempArray[3];
            double charge = Double.valueOf(tempArray[4]);
            int period = Integer.valueOf(tempArray[5]);
            double minSum = Double.valueOf(tempArray[6]);
            double needToSent = Double.valueOf(tempArray[7]);
            double sentAmount = Double.valueOf(tempArray[8]);
            LocalDate lastSent;
            if (getLastSentDate(tempArray[9]) == null) {
                lastSent = null;
            } else {
                lastSent = getLastSentDate(tempArray[9]);
            }
            merchantsList.add(new Merchant(name, bankName, swift,
                    account, charge, period, minSum, needToSent, sentAmount, lastSent));
        }
        return merchantsList;
    }

    public LocalDate getLastSentDate(String str) {
        String receivedDateString = str.toUpperCase();
        LocalDate date = null;
        if (!receivedDateString.equals("NULL")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/dd");
            date = LocalDate.parse(str, formatter);
        } else {
            date = null;
        }
        return date;
    }

    public Merchant getByName(String merchantName) {
        return merchantRepo.getByName(merchantName, false);
    }

    public List<Merchant> getAll() {
        return merchantRepo.getAll();
    }

    public boolean save(Merchant merchant) {
        merchantRepo.save(merchant);
        return true;
    }

    public Merchant getById(int merchantId) {
        return merchantRepo.getById(merchantId, false);
    }

    public List<Merchant> getSortedListInAlphabeticalOrder() {
        List<Merchant> merchantList = getAll();
        Collections.sort(merchantList, new LexicographicComparator());
        return merchantList;
    }

    class LexicographicComparator implements Comparator<Merchant> {
        @Override
        public int compare(Merchant a, Merchant b) {
            return a.getName().compareToIgnoreCase(b.getName());
        }
    }

    public boolean sendFundsToMerchants(Merchant merchant) {
        double totalSumPaid = getTotalSumPaid(merchant);
        if (merchant.getMinSum() < totalSumPaid) {
            merchant.setSentAmount(totalSumPaid);
            merchant.setLastSent(LocalDate.now());
            merchant.setNeedToSend(0.00);
            merchantRepo.update(merchant);
            System.out.println(merchant.getName() + " Payment records were updated.");
        }
        return true;
    }

    public double getTotalSumPaid(Merchant merchant) {
        double totalSumPaid = 0;
        for(Payment p : merchant.getPaymentsList()) {
            totalSumPaid += p.getSumPaid();
        }
        return totalSumPaid;
    }
}