package uk.gov.pay.adminusers.persistence.entity;

import com.google.common.collect.ImmutableList;
import uk.gov.pay.adminusers.model.Service;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static uk.gov.pay.adminusers.app.util.RandomIdGenerator.randomUuid;

@Entity
@Table(name = "services")
@SequenceGenerator(name = "services_seq_gen", sequenceName = "services_id_seq", allocationSize = 1)
public class ServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "services_seq_gen")
    private Integer id;

    @Column(name = "external_id")
    private String externalId;

    @Column(name = "name")
    private String name = Service.DEFAULT_NAME_VALUE;

    @Embedded
    private MerchantDetailsEntity merchantDetailsEntity;

    @Column(name = "custom_branding", columnDefinition = "json")
    @Convert(converter = CustomBrandingConverter.class)
    private Map<String, Object> customBranding;

    @OneToMany(mappedBy = "service", targetEntity = GatewayAccountIdEntity.class, fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private List<GatewayAccountIdEntity> gatewayAccountIds = new ArrayList<>();

    @OneToMany(mappedBy = "service", targetEntity = InviteEntity.class, fetch = FetchType.LAZY)
    private List<InviteEntity> invites = new ArrayList<>();

    public ServiceEntity() {
    }

    public ServiceEntity(List<String> gatewayAccountIds) {
        this.gatewayAccountIds.clear();
        this.externalId = randomUuid();
        populateGatewayAccountIds(gatewayAccountIds);
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MerchantDetailsEntity getMerchantDetailsEntity() {
        return merchantDetailsEntity;
    }

    public void setMerchantDetailsEntity(MerchantDetailsEntity merchantDetailsEntity) {
        this.merchantDetailsEntity = merchantDetailsEntity;
    }

    public List<GatewayAccountIdEntity> getGatewayAccountIds() {
        return ImmutableList.copyOf(this.gatewayAccountIds);
    }

    public GatewayAccountIdEntity getGatewayAccountId() {
        return gatewayAccountIds.get(0);
    }

    public List<InviteEntity> getInvites() {
        return invites;
    }

    public void addGatewayAccountIds(String... gatewayAccountIds) {
        populateGatewayAccountIds(asList(gatewayAccountIds));
    }

    public Map<String, Object> getCustomBranding() {
        return customBranding;
    }

    public void setCustomBranding(Map<String, Object> customBranding) {
        this.customBranding = customBranding;
    }

    public Service toService() {
        Service service = Service.from(id, externalId, name);
        service.setGatewayAccountIds(gatewayAccountIds.stream()
                .map(idEntity -> idEntity.getGatewayAccountId())
                .collect(Collectors.toList()));
        service.setCustomBranding(this.customBranding);
        if (this.merchantDetailsEntity != null) {
            service.setMerchantDetails(this.merchantDetailsEntity.toMerchantDetails());
        }
        return service;
    }

    public boolean hasExactGatewayAccountIds(List<String> gatewayAccountIds) {

        if (this.gatewayAccountIds.size() != gatewayAccountIds.size()) {
            return false;
        }

        for (GatewayAccountIdEntity gatewayAccountIdEntity : this.gatewayAccountIds) {
            if (!gatewayAccountIds.contains(gatewayAccountIdEntity.getGatewayAccountId())) {
                return false;
            }
        }

        return true;
    }

    public static ServiceEntity from(Service service) {
        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setName(service.getName());
        serviceEntity.setExternalId(service.getExternalId());
        return serviceEntity;
    }

    private void populateGatewayAccountIds(List<String> gatewayAccountIds) {
        for (String gatewayAccountId : gatewayAccountIds) {
            this.gatewayAccountIds.add(new GatewayAccountIdEntity(gatewayAccountId, this));
        }
    }

}
