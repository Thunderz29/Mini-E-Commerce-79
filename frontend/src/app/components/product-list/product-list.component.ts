import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { ProductCardComponent } from '../product-card/product-card.component';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, ProductCardComponent],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent {
  products = [
    { id: 1, name: 'Laptop Gaming', price: 15000000, image: 'https://via.placeholder.com/200' },
    { id: 2, name: 'Smartphone Pro', price: 8000000, image: 'https://via.placeholder.com/200' },
    { id: 3, name: 'Headphone Wireless', price: 2000000, image: 'https://via.placeholder.com/200' },
    { id: 4, name: 'Smartwatch', price: 2500000, image: 'https://via.placeholder.com/200' },
    { id: 5, name: 'Kamera DSLR', price: 12000000, image: 'https://via.placeholder.com/200' },
    { id: 6, name: 'Keyboard Mechanical', price: 1500000, image: 'https://via.placeholder.com/200' },
    { id: 7, name: 'Mouse Gaming', price: 900000, image: 'https://via.placeholder.com/200' },
    { id: 8, name: 'Monitor 4K', price: 5000000, image: 'https://via.placeholder.com/200' },
    { id: 9, name: 'Speaker Bluetooth', price: 1000000, image: 'https://via.placeholder.com/200' },
    { id: 10, name: 'Tablet Pro', price: 7500000, image: 'https://via.placeholder.com/200' },
    { id: 11, name: 'Power Bank', price: 300000, image: 'https://via.placeholder.com/200' },
    { id: 12, name: 'Router WiFi 6', price: 1500000, image: 'https://via.placeholder.com/200' },
    { id: 13, name: 'Flashdisk 128GB', price: 200000, image: 'https://via.placeholder.com/200' },
    { id: 14, name: 'SSD NVMe 1TB', price: 2000000, image: 'https://via.placeholder.com/200' },
    { id: 15, name: 'Cooling Pad Laptop', price: 500000, image: 'https://via.placeholder.com/200' },
    { id: 16, name: 'Smart TV 55"', price: 8500000, image: 'https://via.placeholder.com/200' },
    { id: 17, name: 'Kursi Gaming', price: 2500000, image: 'https://via.placeholder.com/200' },
    { id: 18, name: 'Drone 4K', price: 15000000, image: 'https://via.placeholder.com/200' },
    { id: 19, name: 'VR Headset', price: 10000000, image: 'https://via.placeholder.com/200' },
    { id: 20, name: 'Microphone Streaming', price: 1700000, image: 'https://via.placeholder.com/200' }
  ];
}
