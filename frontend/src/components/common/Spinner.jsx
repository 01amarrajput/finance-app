import styles from './Spinner.module.css'

export default function Spinner({ fullscreen = false }) {
  if (fullscreen) return (
    <div className={styles.fullscreen}>
      <span className={styles.ring} />
    </div>
  )
  return <span className={styles.ring} />
}
