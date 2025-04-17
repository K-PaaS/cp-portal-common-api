package org.container.platform.common.api.users;

import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Users Specification 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.31
 **/
@Data
public class UsersSpecification implements Specification<Users> {
    private static final long serialVersionUID = 1L;

    private String userType;
    //private String clusterName;
    private List<String> userTypeIn;
    private String nameLike;
    private String cpNamespace;


    @Override
    public Predicate toPredicate(Root<Users> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> restrictions = new ArrayList<>();

//        if (clusterName != null) {
//            restrictions.add(builder.equal(root.get("clusterName"), clusterName));
//        }

        if (userTypeIn != null && !userTypeIn.isEmpty()) {
            restrictions.add(root.get("userType").in(userTypeIn));
        }

        if (nameLike != null) {
            restrictions.add(builder.like(root.get("userId"), "%" + nameLike + "%"));
        }

        restrictions.add(builder.in(root.get("cpNamespace")).value(cpNamespace).not());

        return builder.and(restrictions.toArray(new Predicate[]{}));
    }
}
