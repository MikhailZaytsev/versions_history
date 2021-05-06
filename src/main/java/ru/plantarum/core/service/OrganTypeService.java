package ru.plantarum.core.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.plantarum.core.entity.OrganType;
import ru.plantarum.core.entity.TradeMark;
import ru.plantarum.core.repository.OrganTypeRepository;
import ru.plantarum.core.web.paging.Direction;
import ru.plantarum.core.web.paging.Order;
import ru.plantarum.core.web.paging.PagingRequest;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrganTypeService {

    private final OrganTypeRepository organTypeRepository;

    public List<OrganType> findAllInactiveIsNull() {
        return organTypeRepository.findByInactiveIsNull();
    }

    private Page<OrganType> findByContent(@Nullable String content, Pageable pageable) {
        return StringUtils.isBlank(content) ? organTypeRepository.findAll(pageable) :
                organTypeRepository.findByOrganTypeNameContainingIgnoreCase(content, pageable);
    }

    public ru.plantarum.core.web.paging.Page<OrganType> findAll(PagingRequest pagingRequest) {

        String stringToFind = pagingRequest.getColumns().get(1).getSearch().getValue();

        int pageNumber = pagingRequest.getStart() / pagingRequest.getLength();
        Order order = pagingRequest.getOrder().stream()
                .findFirst()
                .orElse(new Order(0, Direction.desc));
        String colToOrder = pagingRequest.getColumns().get(order.getColumn()).getData();
        final PageRequest pageRequest = PageRequest.of(pageNumber, pagingRequest.getLength(),
                Sort.Direction.fromString(order.getDir().name()), colToOrder);
        final Page<OrganType> filteredOrganTypes = findByContent(stringToFind, pageRequest);
        ru.plantarum.core.web.paging.Page<OrganType> page = new ru.plantarum.core.web.paging.Page(filteredOrganTypes);
        page.setDraw(pagingRequest.getDraw());
        return page;
    }

    public boolean editOrganType(Long id, OrganType newOrganType) {
        OrganType organType = organTypeRepository.getOne(id);
        newOrganType.setIdOrganType(id);
        if (!organType.equals(newOrganType)) {
            if (organType.getOrganTypeName().equalsIgnoreCase(newOrganType.getOrganTypeName())) {
                organTypeRepository.save(newOrganType);
                return true;
            }
            else {
                if (!exists(newOrganType.getOrganTypeName())) {
                    organTypeRepository.save(newOrganType);
                    return true;
                }
            }
        }
        return false;
    }

    public Optional<OrganType> getOne(Long id) {
        return Optional.of(organTypeRepository.getOne(id));
    }

    public boolean exists(String name) {
        return organTypeRepository.existsOrganTypeByOrganTypeNameIgnoreCase(name);
    }

    public boolean exists(Long id) {
        return organTypeRepository.existsById(id);
    }

    public OrganType deleteOrganType(Long id) {
        OrganType organType = organTypeRepository.getOne(id);
        if (organType.getInactive() == null) {
            OffsetDateTime dateTime = OffsetDateTime.now();
            organType.setInactive(dateTime);
        }
        return organType;
    }

    public OrganType save(OrganType organType) {
        return organTypeRepository.save(organType);
    }

    public OrganType findByOrganTypeName(String organTypeName) {
        return organTypeRepository.findByOrganTypeNameIgnoreCase(organTypeName);
    }
}
