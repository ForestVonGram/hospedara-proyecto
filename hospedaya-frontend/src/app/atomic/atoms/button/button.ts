import { Component, Input } from '@angular/core';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-button',
  standalone: true,
  imports: [NgClass],
  templateUrl: './button.html',
  styleUrls: ['./button.scss']
})
export class ButtonComponent {
  @Input() type: 'primary' | 'secondary' | 'outline' = 'primary';

  @Input() label: string = 'Click';

  @Input() disabled: boolean = false;
}

