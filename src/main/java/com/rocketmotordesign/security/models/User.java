package com.rocketmotordesign.security.models;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;

@Entity
@Table(	name = "users",
		uniqueConstraints = {
			@UniqueConstraint(columnNames = "email")
		})
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(max = 50)
	@Email
	private String email;

	@NotBlank
	@Size(max = 120)
	private String password;

	@CreatedDate
	private LocalDateTime dateCreation;

	private LocalDateTime derniereConnexion;

	private boolean compteValide;

	/**
	 * Identify prime donators; Every user falg as true make a donation before
	 * donattion reinforcment policy. So they will have a life access to all
	 * donator features
	 */
	private boolean donator = false;

	@Column(name = "last_donation")
	private LocalDateTime lastDonation;

	@Column(name = "receive_newsletter")
	private boolean receiveNewsletter = true;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(	name = "user_roles",
				joinColumns = @JoinColumn(name = "user_id"),
				inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	@Column(name = "stripe_customer_id")
	private String stripeCustomerId;

	public User() {
	}

	public User(String email, String password) {
		this.email = email;
		this.password = password;
		this.compteValide = false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public LocalDateTime getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(LocalDateTime dateCreation) {
		this.dateCreation = dateCreation;
	}

	public LocalDateTime getDerniereConnexion() {
		return derniereConnexion;
	}

	public void updateDerniereConnexion() {
		this.derniereConnexion = now();
	}

	public boolean isCompteValide() {
		return compteValide;
	}

	public void setCompteValide(boolean compteValide) {
		this.compteValide = compteValide;
	}

	public boolean isDonator() {
		return donator;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return compteValide;
	}

	public boolean isReceiveNewsletter() {
		return receiveNewsletter;
	}

	public void setReceiveNewsletter(boolean receiveNewsletter) {
		this.receiveNewsletter = receiveNewsletter;
	}

	public void setDerniereConnexion(LocalDateTime derniereConnexion) {
		this.derniereConnexion = derniereConnexion;
	}

	public void setDonator(boolean donator) {
		this.donator = donator;
	}

	/**
	 * Used to identify active donator. Active donator are user that are flagged as donator,
	 * or user that make a donation after now-durationOfActiveDonation, or user that have created theri account in the last 7 days.
	 * This is used in METEOR to show the donation popup before computation
	 * @param durationOfActiveDonation time of active donation
	 * @param now the current time
	 * @return true if now-durationOfActiveDonation<lastDonation or if isDonator or dateCreation is in the lmast 7 days otherwise false
	 */
	public boolean isActiveDonator(Duration durationOfActiveDonation, LocalDateTime now) {
		if(isDonator() || (dateCreation != null && Duration.between(dateCreation, now).toDays() <= 7)) {
			return true;
		} else if(lastDonation != null){
			return Duration.between(lastDonation, now).toDays() <= durationOfActiveDonation.toDays() ;
		} else {
			return false;
		}
	}

	public LocalDateTime getLastDonation() {
		return lastDonation;
	}

	public void setLastDonation(LocalDateTime lastDonation) {
		this.lastDonation = lastDonation;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		User user = (User) o;
		return Objects.equals(id, user.id);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getName().name()))
				.collect(Collectors.toList());
	}

	public String getStripeCustomerId() {
		return stripeCustomerId;
	}

	public void setStripeCustomerId(String stripeCustomerId) {
		this.stripeCustomerId = stripeCustomerId;
	}
}
