package com.wnc.banking.service;

import com.wnc.banking.dto.ChangePasswordRequest;
import com.wnc.banking.dto.CustomerDTO;
import com.wnc.banking.entity.Account;
import com.wnc.banking.entity.Customer;
import com.wnc.banking.repository.CustomerRepository;
import com.wnc.banking.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public String createCustomer(CustomerDTO customerDto) {
        Customer customer = new Customer();

        customer.setName(customerDto.getName());
        customer.setAddress(customerDto.getAddress());
        customer.setPhoneNumber(customerDto.getPhoneNumber());
        customer.setEmail(customerDto.getEmail());

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        customer.setPassword(bCryptPasswordEncoder.encode(customerDto.getPassword()));

        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());

        String accountNumber;
        do {
            accountNumber = "0" + new Random().ints(11, 0, 10)
                    .mapToObj(String::valueOf)
                    .collect(Collectors.joining());
        } while (paymentRepository.existsByAccountNumber(accountNumber));

        Account account = new Account();
        account.setAccountNumber(accountNumber);
        customer.setAccount(account);
        account.setCustomer(customer);

        customerRepository.save(customer);
        paymentRepository.save(account);

        return "Create customer successfully";
    }

    public String updateCustomer(String email, CustomerDTO customerDto) {
        Customer customer = customerRepository.findByEmail(email);
        if (customer == null) {
            return "Cannot found customer with email: " + email;
        }

        if (customerDto.getName() != null && !customerDto.getName().isEmpty()) {
            customer.setName(customerDto.getName());
        }
        if (customerDto.getEmail() != null && !customerDto.getEmail().isEmpty()) {
            customer.setEmail(customerDto.getEmail());
        }
        if (customerDto.getAddress() != null && !customerDto.getAddress().isEmpty()) {
            customer.setAddress(customerDto.getAddress());
        }
        if (customerDto.getPhoneNumber() != null && !customerDto.getPhoneNumber().isEmpty()) {
            customer.setPhoneNumber(customerDto.getPhoneNumber());
        }

        customer.setUpdatedAt(LocalDateTime.now());

        customerRepository.save(customer);

        return "Update customer successfully";
    }

    public String changePassword(String email, ChangePasswordRequest changePasswordRequest) {
        String oldPassword = changePasswordRequest.getOldPassword();
        String newPassword = changePasswordRequest.getNewPassword();
        if (oldPassword.equals(newPassword)) {
            return "New password must be different from old password";
        }

        Customer customer = customerRepository.findByEmail(email);
        if (customer == null) {
            return "Cannot found customer with email: " + email;
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if (!bCryptPasswordEncoder.matches(oldPassword, customer.getPassword())) {
            return "Old password is incorrect";
        }

        customer.setPassword(bCryptPasswordEncoder.encode(newPassword));
        customer.setUpdatedAt(LocalDateTime.now());

        customerRepository.save(customer);

        return "Change password successfully";
    }
}
