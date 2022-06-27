package jam
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.spi.CurrencyNameProvider;

/**
 * Genre, Title 을 관리하는 영화 데이터베이스.
 * 
 * MyLinkedList 를 사용해 각각 Genre와 Title에 따라 내부적으로 정렬된 상태를  
 * 유지하는 데이터베이스이다. 
 */
public class MovieDB {
	private MyLinkedList<Genre> genres = new MyLinkedList<>();
	public MyLinkedList<MovieList> movies = new MyLinkedList<>();
	private MyLinkedList<MovieDBItem> All_Item = new MyLinkedList<>();


	public MovieDB() {
        // FIXME implement this
    	// HINT: MovieDBGenre 클래스를 정렬된 상태로 유지하기 위한 
    	// MyLinkedList 타입의 멤버 변수를 초기화 한다.
		genres = new MyLinkedList<>();
		movies = new MyLinkedList<>();
		All_Item = new MyLinkedList<>();
    }

    public void insert(MovieDBItem item) {
        // FIXME implement this
        // Insert the given item to the MovieDB.
		Node<Genre> newGenre = new Node<>(new Genre(item.getGenre()));
		Node<Genre> currGenre = genres.head;
		Node<MovieList> currMovie = movies.head;
		Node<MovieList> newMovie;


		if (genres.isEmpty()){

			// Genre 추가
			genres.head.setNext(newGenre);

			// MovieList 추가
			newMovie = new Node<>(new MovieList());
			movies.head.setNext(newMovie);
			newMovie.getItem().head = newGenre.getItem();
			newMovie.getItem().add(item.getTitle());

		}

		else{
			while(currGenre.getNext()!= null){
				if(currGenre.getNext().getItem().compareTo(newGenre.getItem()) < 0) { //newNode가 더 뒤에 존재
					currGenre = currGenre.getNext();

				}
				else if(currGenre.getNext().getItem().compareTo(newGenre.getItem()) > 0){ //newNode 때려박기. 새로운 추가
					newGenre.setNext(currGenre.getNext());
					currGenre.setNext(newGenre);


					// MovieList 추가

					// newMovie Node 준비
					newMovie = new Node<>(new MovieList());
					newMovie.getItem().head = newGenre.getItem();
					newMovie.getItem().add(item.getTitle());

					// newMovie Node 삽입
					while(currMovie.getNext() != null){

						if(currMovie.getNext().getItem().head.equals(newGenre.getNext().getItem())){
							newMovie.setNext(currMovie.getNext());
							currMovie.setNext(newMovie);
							return;
						}
						else {
							currMovie = currMovie.getNext();
						}
					}


				}
				// 이미 Genre 존재하는 경우
				else{
					while(currMovie.getNext() != null) {
						if (currMovie.getNext().getItem().head.equals(currGenre.getNext().getItem())) {
							currMovie.getNext().getItem().add(item.getTitle());
							return;
						}
						currMovie = currMovie.getNext();
					}


				}
			}

			if(currGenre.getNext() == null){
				currGenre.setNext(newGenre);

				// MovieList 추가
				newMovie = new Node<>(new MovieList());
				movies.add(newMovie.getItem());
				newMovie.getItem().head = newGenre.getItem();
				newMovie.getItem().add(item.getTitle());
			}
		}

    }

    public void delete(MovieDBItem item) {
        // FIXME implement this
        // Remove the given item from the MovieDB.

		Node<MovieList> CurrMovie = this.movies.head;
		Node<Genre> CurrGenre = this.genres.head;

		while(CurrMovie.getNext() != null){
			if(CurrMovie.getNext().getItem().head.getItem().equals(item.getGenre())){
				CurrMovie.getNext().getItem().remove(item.getTitle());
				if(CurrMovie.getNext().getItem().size() == 0){
					movies.remove(CurrMovie.getNext().getItem());;
					while (CurrGenre.getNext() != null){
						if(CurrGenre.getNext().getItem().getItem().compareTo(item.getGenre())==0){
							CurrGenre.setNext(CurrGenre.getNext().getNext());
							genres.numItems -= 1;
							return;
						}
						CurrGenre = CurrGenre.getNext();
					}
				}
				return;
			}
			CurrMovie = CurrMovie.getNext();
		}


    	// Printing functionality is provided for the sake of debugging.
        // This code should be removed before submitting your work.
    }

    public MyLinkedList<MovieDBItem> search(String term) {
        // FIXME implement this
        // Search the given term from the MovieDB.
        // You should return a linked list of MovieDBItem.
        // The search command is handled at SearchCmd class.
		Node<MovieList> CurrMovie = movies.head;
		MyLinkedList<MovieDBItem> result = new MyLinkedList<>();
		MovieList movielist;

		while (CurrMovie.getNext() != null){
			movielist = CurrMovie.getNext().getItem().search(term);
			for(String movie : movielist){
				result.add(new MovieDBItem(CurrMovie.getNext().getItem().head.getItem(),movie));
			}
			CurrMovie = CurrMovie.getNext();
		}

		return result;

    }
    
    public MyLinkedList<MovieDBItem> items() {
		return this.search("");
    }
}

class Genre extends Node<String> implements Comparable<Genre> {
	public Genre(String name) {
		super(name);
	}

	@Override
	public int compareTo(Genre o) {
		return this.getItem().compareTo(o.getItem());
	}

	@Override
	public int hashCode() {
		return this.getItem().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this != obj) {
			return false;
		}
		else if (obj instanceof Genre) {
			Genre anotherGenre = (Genre) obj;
			if (this.compareTo(anotherGenre) == 0) {
				return true;
			}
		}
		return false;
	}
}

class MovieList implements ListInterface<String> {
	MyLinkedList<String> movies = new MyLinkedList<>();
	Node<String> head;

	public MovieList() {
		movies = new MyLinkedList<>();
	}

	@Override
	public Iterator<String> iterator() {
		return new MyLinkedListIterator(this.movies);
	}

	@Override
	public boolean isEmpty() {
		return this.movies.isEmpty();
	}

	@Override
	public int size() {
		return this.movies.size();
	}


	@Override
	public void add(String item) {
		Node<String> currNode = this.movies.head;
		Node<String> newNode = new Node(item);
		int countSame = 0;


		loop:
		while (currNode.getNext() != null) {
			String chNext = currNode.getNext().getItem();
			String chNew = newNode.getItem();
			countSame = 0;
			for (int i = 0; i < chNext.length() && i < chNew.length(); ++i) {


				if ((int) chNext.charAt(i) > (int) chNew.charAt(i)) {


					newNode.setNext(currNode.getNext());

					currNode.setNext(newNode);

					this.movies.numItems += 1;
					break loop;

				} else if ((int) chNext.charAt(i) < (int) chNew.charAt(i)) {


					currNode = currNode.getNext();

					break;

				} else {
					countSame += 1;
					if (countSame == chNext.length() && countSame == chNew.length()) {

						break loop;
					} else if(countSame == chNew.length()) {
						newNode.setNext(currNode.getNext());
						currNode.setNext(newNode);
						this.movies.numItems += 1;
						break loop;
					} else if(countSame == chNext.length()){
						currNode = currNode.getNext();

						break;


					}
					else {
						continue;
					}
					}
				}


		}

		if (currNode.getNext() == null) {
			this.movies.add(item);
		}

	}

	@Override
	public String first() {
		return this.movies.first();
	}

	@Override
	public void removeAll() {
		this.movies.removeAll();
	}

	@Override
	public void remove(String item){
		this.movies.remove(item);
	}

	public MovieList search(String item){
		Node<String> CurrNode = this.movies.head;
		MovieList matched_Strings = new MovieList();

		while (CurrNode.getNext() != null){
			if(CurrNode.getNext().getItem().contains(item)){
				matched_Strings.add(CurrNode.getNext().getItem());
			}
			CurrNode = CurrNode.getNext();
		}
		return matched_Strings;
	}
}
