import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
  isMenuOpen = false;
  isUserMenuOpen = false;
  cartItemCount = 3;
  orderItemCount = 2;
  userName = 'User'; 
  searchQuery = ''; 

  constructor(private router: Router) {}

  @Output() searchEvent = new EventEmitter<string>();

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }

  toggleUserMenu() {
    this.isUserMenuOpen = !this.isUserMenuOpen;
  }

  logout(event: Event) {
    event.stopPropagation();
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }
  
  onSearch() {
    this.searchEvent.emit(this.searchQuery);
  }

  navigateToCart(event: Event) {
    event.stopPropagation();
    this.router.navigate(['/cart']);
  }

  navigateToOrderStatus(event: Event) {
    event.stopPropagation();
    this.router.navigate(['/Order-status']);
  }

  navigateToProfile(event: Event) {
    event.stopPropagation();
    this.router.navigate(['/profile']);
  }

  navigateToOrder(event: Event) {
    event.stopPropagation();
    this.router.navigate(['/order-history']);
  }
}