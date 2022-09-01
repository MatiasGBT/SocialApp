import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-newpost',
  templateUrl: './newpost.component.html',
  styleUrls: ['./newpost.component.css']
})
export class NewpostComponent implements OnInit {
  files: File[] = [];
  public placeholder: string;

  constructor(private translate: TranslateService) { }

  ngOnInit(): void {
    let lang = localStorage.getItem("lang");
    if (lang == null) {
      lang = "en";
    }
    this.translate.get('NEWPOST.PLACEHOLDER').subscribe((res: string) => {
      this.placeholder = res;
    });
  }

  onSelect(event) {
    this.files.push(...event.addedFiles);
    if(this.files.length > 1){
      this.replaceFile();
    }
  }
  
  onRemove(event) {
    this.files.splice(this.files.indexOf(event), 1);
  }

  replaceFile(){
    this.files.splice(0, 1);
  }
}
