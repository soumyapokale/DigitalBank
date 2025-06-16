package com.bank.DigitalBank.Repository;


import com.bank.DigitalBank.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {


    User findByEmail(String email);
}
