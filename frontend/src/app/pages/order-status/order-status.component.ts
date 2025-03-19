import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { NavbarComponent } from '../../components/navbar/navbar.component';

@Component({
  selector: 'app-order-status',
  standalone: true,
  templateUrl: './order-status.component.html',
  styleUrls: ['./order-status.component.css'],
  imports: [CommonModule, NavbarComponent] // Import yang dibutuhkan
})
export class OrderStatusComponent {
  orders = [
    {
      id: 'INV-20240319-001',
      image: 'assets/product1.jpg',
      name: 'Sneakers Sport',
      qty: 2,
      price: 250000,
      status: 'Sedang Diproses'
    },
    {
      id: 'INV-20240319-002',
      image: 'assets/product2.jpg',
      name: 'Jacket Hoodie',
      qty: 1,
      price: 350000,
      status: 'Dikirim'
    }
  ];

  getStatusClass(status: string) {
    switch (status) {
      case 'Sedang Diproses': return 'status-processing';
      case 'Dikirim': return 'status-shipped';
      case 'Selesai': return 'status-completed';
      default: return '';
    }
  }
}
