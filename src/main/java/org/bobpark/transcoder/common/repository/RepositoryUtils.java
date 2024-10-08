package org.bobpark.transcoder.common.repository;

import static org.apache.commons.lang3.ObjectUtils.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;

public interface RepositoryUtils {

    static OrderSpecifier<?>[] sort(Pageable pageable, List<QueryDslPath<?>> paths) {
        List<OrderSpecifier<?>> sortList = Lists.newArrayList();

        if (!pageable.getSort().isEmpty()) {
            for (Sort.Order order : pageable.getSort()) {
                String property = order.getProperty();

                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

                QueryDslPath<?> queryDslPath = paths.stream()
                    .filter(item -> StringUtils.equals(item.name(), property))
                    .findAny()
                    .orElse(null);

                if (isEmpty(queryDslPath)) {
                    continue;
                }

                sortList.add(new OrderSpecifier<>(direction, queryDslPath.path()));

            }
        }

        return sortList.toArray(new OrderSpecifier[] {});
    }

}
