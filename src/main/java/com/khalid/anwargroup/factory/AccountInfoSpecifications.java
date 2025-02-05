package com.khalid.anwargroup.factory;

import com.khalid.anwargroup.domain.AccountInfo;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class AccountInfoSpecifications {

    public static Specification<AccountInfo> searchByTerm(String term) {
        return (root, query, criteriaBuilder) -> {
            if (term == null || term.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String likeTerm = "%" + term.toLowerCase() + "%";

            Predicate firstNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), likeTerm);
            Predicate lastNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), likeTerm);
            Predicate usernamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), likeTerm);
            Predicate phoneNumberPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("phoneNumber")), likeTerm);
            Predicate locationPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("location")), likeTerm);

            Predicate combinedPredicate = criteriaBuilder.or(firstNamePredicate, lastNamePredicate, usernamePredicate, phoneNumberPredicate, locationPredicate);

            return criteriaBuilder.and(combinedPredicate, criteriaBuilder.isFalse(root.get("deleteFlg")));
        };
    }
}
