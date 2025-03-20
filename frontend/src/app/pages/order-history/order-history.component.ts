import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NavbarComponent } from '../../components/navbar/navbar.component';

@Component({
  selector: 'app-order-history',
  standalone: true, 
  imports: [CommonModule, FormsModule, RouterModule, NavbarComponent],
  templateUrl: './order-history.component.html',
  styleUrls: ['./order-history.component.css']
})
export class OrderHistoryComponent {
  orders = [
    {
      id: 'TRX123456',
      totalItems: 3,
      totalPrice: 15925000,
      completedAt: '18 Maret 2025',  
      products: [
        { name: 'Laptop Gaming', price: 15000000, image: 'images/product.jpg' },
        { name: 'Mouse Wireless', price: 250000, image: 'images/product.jpg' },
        { name: 'Headset Gaming', price: 750000, image: 'images/product.jpg' }
      ]
    },
    {
      id: 'TRX789012',
      totalItems: 2,
      totalPrice: 3700000,
      completedAt: '15 Maret 2025',
      products: [
        { name: 'Keyboard Mechanical', price: 1200000, image: 'images/product.jpg' },
        { name: 'Monitor 24 inch', price: 2500000, image: 'images/product.jpg' }
      ]
    }
  ];
}
