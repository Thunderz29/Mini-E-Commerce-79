import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './product-card.component.html',
  styleUrls: ['./product-card.component.css']
})
export class ProductCardComponent {
  @Input() product!: { id: number; name: string; price: number; image: string };

  constructor(private router: Router) {}

  navigateToDetail(event: Event) {
    event.stopPropagation();
    this.router.navigate(['/product']);
  }
}
