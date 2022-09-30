import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';

@Injectable({
  providedIn: 'root'
})
export class CatchErrorService {

  constructor() { }

  public showErrorModal(e: any) {
    Swal.fire({
      icon: 'error', title: e.error.message, text: e.error.error, showConfirmButton: false,
      timer: 1500, background: '#7f5af0', color: 'white'
    });
  }
}
