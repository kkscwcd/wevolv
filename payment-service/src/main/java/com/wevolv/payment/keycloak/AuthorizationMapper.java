package com.wevolv.payment.keycloak;

import com.wevolv.payment.exception.UnauthorizedException;
import com.wevolv.payment.keycloak.authorization.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class AuthorizationMapper {

    private static final int SPORT_INDEX = 0;
    private static final int UNION_SP_INDEX = 1;
    private static final int UNION_NAME_INDEX = 2;
    private static final int UNION_ROLE_INDEX = 3;

    public GroupAuthorization map(final List<String> groups) {
        final List<GroupElement> elements = groups.stream().map(this::map).collect(Collectors.toList());
        return merge(elements);
    }

    public GroupElement map(String group) {
        group = group.replaceFirst("/", "");
        final List<String> splittedGroups = Stream.of(group.split("/")).collect(Collectors.toList());

        return GroupElement.builder()
                .sport(extractSport(splittedGroups))
                .serviceProvider(extractServiceProvider(splittedGroups))
                .union(extractUnion(splittedGroups).orElse(null))
                .build();
    }

    public Sport extractSport(final List<String> splittedGroups) {
        var sport = splittedGroups.get(SPORT_INDEX);
        return Sport.valueOf(sport.toUpperCase());
    }

    public boolean extractServiceProvider(final List<String> splittedGroups) {
        if (splittedGroups.size() < (UNION_SP_INDEX + 1)) {
            return false;
        }
        var groupValue = splittedGroups.get(UNION_SP_INDEX);
        return Group.SERVICE_PROVIDER.equals(Group.valueOf(groupValue.toUpperCase()));
    }

    public Optional<Union> extractUnion(final List<String> splittedGroups) {
        if (splittedGroups.size() < (UNION_SP_INDEX + 1)) {
            return Optional.empty();
        }

        if (splittedGroups.size() < (UNION_NAME_INDEX + 1)) {
            return Optional.empty();
        }

        if (splittedGroups.size() < (UNION_ROLE_INDEX + 1)) {
            return Optional.empty();
        }

        return Optional.of(
                Union.builder()
                        .name(splittedGroups.get(UNION_NAME_INDEX))
                        .role(UnionRole.valueOf(splittedGroups.get(UNION_ROLE_INDEX).toUpperCase()))
                        .build());
    }
    public GroupAuthorization merge(List<GroupElement> groupElements) {
        var isServiceProvider = groupElements.stream()
                .anyMatch(GroupElement::isServiceProvider);

        var sports = groupElements.stream()
                .map(GroupElement::getSport)
                .collect(Collectors.toSet());

        if (sports.size() > 1) {
            throw new UnauthorizedException("User should belong to only one sport");
        }

        var sport = sports.stream().findAny();

        final Map<String, List<UnionRole>> roles = groupElements.stream()
                .filter(x -> !ObjectUtils.isEmpty(x.getUnion()))
                .collect(Collectors.groupingBy(x -> x.getUnion().getName(),
                        Collectors.mapping(element -> element.getUnion().getRole(),
                                Collectors.toList())));

        return GroupAuthorization.builder()
                .isServiceProvider(isServiceProvider)
                .sport(sport.orElse(null))
                .unions(roles)
                .build();
    }
}
