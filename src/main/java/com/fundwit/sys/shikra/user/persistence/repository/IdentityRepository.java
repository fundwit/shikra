package com.fundwit.sys.shikra.user.persistence.repository;

import com.fundwit.sys.shikra.user.persistence.po.Identity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdentityRepository extends JpaRepository<Identity, Long> {
    Identity findByUserIdAndType(Long userId, String type);
}
